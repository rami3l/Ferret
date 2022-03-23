package fr.ferret.controller.input;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import fr.ferret.controller.input.common.NeedingConversionPanelController;
import fr.ferret.controller.state.Error;
import fr.ferret.model.locus.Locus;
import fr.ferret.model.locus.VariantConversion;
import fr.ferret.model.state.PublishingStateProcessus;
import fr.ferret.model.utils.FileReader;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.VariantPanel;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link VariantPanel} controller
 */
public class VariantPanelController extends NeedingConversionPanelController {

    /** The panel which is controlled by this controller */
    private final VariantPanel panel;

    private static final Logger logger = Logger.getLogger(VariantPanelController.class.getName());

    public VariantPanelController(FerretFrame frame) {
        super(frame);
        panel =  frame.getVariantPanel();
    }

    public void validateInfoAndRun() {
        // Reset the borders
        panel.getInputField().setBorder(null);
        panel.getFileSelector().getRunButton().setBorder(null);
        panel.getBpField().setBorder(null);

        JTextField geneNameField = panel.getInputField();

        // Selected populations for the model
        var populations = getSelectedPopulations();
        boolean popSelected = !populations.isEmpty();

        // List which will contain the genes (from field or file)
        List<String> snpList = null;

        String snpString = geneNameField.getText();

        // Did the user input a list of gene
        boolean snpListInputted = snpString.length() > 0;

        // Did the user import a csv file
        String snpFileNameAndPath = panel.getFileSelector().getSelectedFile() == null ?
            null :
            panel.getFileSelector().getSelectedFile().getAbsolutePath();
        boolean snpFileImported = snpFileNameAndPath != null;

        // Are they errors in imported file (impossible to read, invalid extension or invalid
        // content)
        boolean snpFileError = false;
        boolean snpFileExtensionError = false;
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

        // invalid characters for the genes (inputted as a list or a file)
        String invalidRegex = ".*\\D.*"; // This is everything except numbers

        if (snpFileImported) {

            try {
                snpList = FileReader.readCsvLike(snpFileNameAndPath, invalidRegex);
            } catch (FileFormatException e) {
                snpFileExtensionError = true;
            } catch (FileContentException e) {
                invalidCharacter = true;
            } catch (IOException e) {
                snpFileError = true;
            }

        } else if (snpListInputted) {
            snpString = snpString.replace(" ", "");
            invalidCharacter = snpString.replace(",", "").matches(invalidRegex);
            if (snpString.endsWith(",")) {
                snpString = snpString.substring(0, snpString.length() - 1);
            }
            snpList = Arrays.asList(snpString.split(","));
        }

        if ((snpListInputted || (snpFileImported && !snpFileError && !snpFileExtensionError)) && !invalidCharacter && validWindowSizeEntered && popSelected) {

            convertAndDownloadVcf(populations, snpList, windowSize);

        } else {
            displayError(snpListInputted, snpFileImported, snpFileError, snpFileExtensionError,
                invalidCharacter, popSelected, validWindowSizeEntered);
        }
    }

    @Override
    protected boolean confirmContinue(String notFound) {
        return ExceptionHandler.variantsNotFoundMessage(notFound);
    }

    @Override
    protected PublishingStateProcessus<List<Locus>> getConversionProcessus(List<String> variantList) {
        var hgVersion = Resource.config().getSelectedHumanGenome().toGRC();
        logger.log(Level.INFO, "Starting gene research using {0} HG version...", hgVersion);
        return new VariantConversion(variantList, hgVersion);
    }



    private void displayError(boolean snpListInputted, boolean snpFileImported,
        boolean snpFileError, boolean snpFileExtensionError, boolean invalidCharacter,
        boolean popSelected, boolean validWindowSizeEntered) {

        var error = new Error(frame).append("run.fixerrors");

        var idSelector = panel.getInputField();
        var runButton = panel.getFileSelector().getRunButton();

        if (!snpListInputted && !snpFileImported) {
            error.append("run.selectvari").highlight(idSelector, runButton);
        }
        if (snpFileImported && snpFileError) {
            error.append("run.selectvari.ferr").highlight(runButton);
        }
        if (snpFileImported && snpFileExtensionError) {
            error.append("run.selectvari.fext").highlight(runButton);
        }
        if ((snpListInputted || snpFileImported) && invalidCharacter) {
            error.append("run.selectvari.cerr")
                    .highlight(snpListInputted ? idSelector : runButton);
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
