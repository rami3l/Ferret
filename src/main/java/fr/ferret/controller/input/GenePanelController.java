package fr.ferret.controller.input;

import fr.ferret.controller.exceptions.ConversionIncompleteException;
import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import fr.ferret.controller.input.common.NeedingConversionPanelController;
import fr.ferret.controller.state.Error;
import fr.ferret.model.ZoneSelection;
import fr.ferret.model.locus.GeneConversion;
import fr.ferret.model.locus.Locus;
import fr.ferret.model.locus.VariantConversion;
import fr.ferret.model.state.PublishingStateProcessus;
import fr.ferret.model.state.State;
import fr.ferret.model.utils.FileReader;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.GenePanel;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link GenePanel} controller
 */
public class GenePanelController extends NeedingConversionPanelController {

    /** The panel which is controlled by this controller */
    private final GenePanel panel;

    private static final Logger logger = Logger.getLogger(GenePanelController.class.getName());

    public GenePanelController(FerretFrame frame) {
        super(frame);
        panel = frame.getGenePanel();
    }

    public void validateInfoAndRun() {
        // Reset the borders
        panel.getInputField().setBorder(null);
        panel.getFileSelector().getRunButton().setBorder(null);

        JTextField geneField = panel.getInputField();

        // Selected populations for the model
        var populations = getSelectedPopulations();
        boolean popSelected = !populations.isEmpty();

        // List which will contain the genes (from field or file)
        List<String> geneList = null;

        String geneString = geneField.getText().replace(" ", "");

        // Did the user input a list of gene
        boolean geneListInputted = geneString.length() > 0;

        // Did the user import a csv file
        String geneFileNameAndPath = panel.getFileSelector().getSelectedFile() == null ? null
                : panel.getFileSelector().getSelectedFile().getAbsolutePath();
        boolean geneFileImported = geneFileNameAndPath != null;

        // Are they errors in imported file (impossible to read, invalid extension or invalid
        // content)
        boolean geneFileError = false;
        boolean geneFileExtensionError = false;
        boolean invalidCharacter = false;

        // windowSize field must be an integer or be empty (0 as default value)
        String snpWindowSizeText = panel.getBpField().getText();
        boolean validWindowSizeEntered = true;
        int windowSize = 0;

        if(!snpWindowSizeText.isBlank()) {
            try {
                windowSize = Integer.parseInt(snpWindowSizeText);
            } catch (Exception e) {
                validWindowSizeEntered = false;
            }
        }

        // Invalid characters for the genes names/ids (inputted as a list or a file)
        // This is everything except letters and numbers, including underscore
        var invalidRegex = ".*[^a-zA-Z0-9\\-].*";

        if (geneFileImported) {

            try {
                geneList = FileReader.readCsvLike(geneFileNameAndPath, invalidRegex);
            } catch (FileFormatException e) {
                geneFileExtensionError = true;
            } catch (FileContentException e) {
                invalidCharacter = true;
            } catch (IOException e) {
                geneFileError = true;
            }

        } else if (geneListInputted) {
            invalidCharacter = geneString.replace(",", "").matches(invalidRegex);
            if (geneString.endsWith(",")) {
                geneString = geneString.substring(0, geneString.length() - 1);
            }
            geneList = Arrays.asList(geneString.split(","));
        }

        if ((geneListInputted || (geneFileImported && !geneFileError && !geneFileExtensionError))
                && !invalidCharacter && popSelected) {

            // TODO: What is locale "all" ? Locale.ROOT ?
            var locale = new Locale("all");
            geneList = geneList.stream().map(text -> text.toUpperCase(locale)).toList();

            convertAndDownloadVcf(populations, geneList, windowSize);

        } else {
            displayError(geneListInputted, geneFileImported, geneFileError, geneFileExtensionError,
                    invalidCharacter, popSelected, validWindowSizeEntered);
        }
    }

    @Override
    protected boolean confirmContinue(String notFound) {
        return ExceptionHandler.variantsNotFoundMessage(notFound);
    }

    @Override
    protected PublishingStateProcessus<List<Locus>> getConversionProcessus(List<String> geneList) {
        var assemblyAccVer = Resource.getAssemblyAccessVersion();
        logger.log(Level.INFO, "Starting gene research using {0} assembly accession version...", assemblyAccVer);
        return new GeneConversion(geneList, assemblyAccVer);
    }


    private void displayError(boolean geneListInputted, boolean geneFileImported,
            boolean geneFileError, boolean geneFileExtensionError, boolean invalidCharacter,
            boolean popSelected, boolean validWindowSizeEntered) {

        JComponent inputField = panel.getInputField();
        JComponent runButton = panel.getFileSelector().getRunButton();

        var error = new Error(frame).append("run.fixerrors");

        if (!geneListInputted && !geneFileImported) {
            error.append("run.selectgene").highlight(inputField, runButton);
        }
        if (geneFileImported && geneFileError) {
            error.append("run.selectgene.ferr").highlight(runButton);
        }
        if (geneFileImported && geneFileExtensionError) {
            error.append("run.selectgene.fext").highlight(runButton);
        }
        if (geneListInputted && invalidCharacter) {
            error.append("run.selectgene.cerr").highlight(inputField);
        }
        if (geneFileImported && invalidCharacter) {
            error.append("run.selectgene.cerr").highlight(runButton);
        }
        if (!popSelected) {
            error.append("run.selectpop").highlight(frame.getRegionPanel());
        }
        if (!validWindowSizeEntered) {
            error.append("run.selectvari.wsize").highlight(panel.getBpField());
        }
        error.show();
    }
}
