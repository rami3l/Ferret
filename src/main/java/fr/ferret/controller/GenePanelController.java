package fr.ferret.controller;

import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import fr.ferret.model.utils.FileReader;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.GenePanel;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * The {@link GenePanel} controller
 */
public class GenePanelController extends InputPanelController<GenePanel> {

    private static final Logger logger = Logger.getLogger(GenePanelController.class.getName());

    public GenePanelController(FerretFrame frame) {
        super(frame, frame.getGenePanel());
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

        String geneString = geneField.getText();

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
            geneString = geneString.replace(" ", "");
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

            // TODO LINK WITH MODEL - see LocusPanelController to know how to deal with the file

        } else {
            displayError(geneListInputted, geneFileImported, geneFileError, geneFileExtensionError,
                    invalidCharacter, popSelected);
        }
    }


    // private void downloadVcf(ZoneSelection populations, String chr,
    // final int start, final int end) {
    // run(outFile -> {
    // logger.log(Level.INFO, "Starting gene research...");
    // var isgrClient =
    // IgsrClient.builder().chromosome(chr).phase1KG(Resource.CONFIG.getSelectedVersion()).build();
    // var download = frame.getBottomPanel().addState("Starting download", outFile);
    // isgrClient.exportVCFFromSamples(outFile, start, end, populations)
    // .doOnComplete(download::complete).doOnError(e -> {
    // logger.log(Level.WARNING, "Error while downloading or writing");
    // download.error();
    // }).subscribe(download::setState);
    // });
    // }

    private void displayError(boolean geneListInputted, boolean geneFileImported,
            boolean geneFileError, boolean geneFileExtensionError, boolean invalidCharacter,
            boolean popSelected) {

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
        error.show();
    }
}
