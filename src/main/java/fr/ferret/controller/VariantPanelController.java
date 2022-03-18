package fr.ferret.controller;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import fr.ferret.controller.exceptions.VariantsNotFoundException;
import fr.ferret.controller.state.Error;
import fr.ferret.model.ZoneSelection;
import fr.ferret.model.locus.VariantConversion;
import fr.ferret.model.state.State;
import fr.ferret.model.utils.FileReader;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.VariantPanel;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link VariantPanel} controller
 */
public class VariantPanelController extends InputPanelController<VariantPanel> {

    private static final Logger logger = Logger.getLogger(VariantPanelController.class.getName());

    public VariantPanelController(FerretFrame frame) {
        super(frame, frame.getVariantPanel());
    }

    public void validateInfoAndRun() {
        // Reset the borders
        panel.getVariantIdField().setBorder(null);
        panel.getFileSelector().getRunButton().setBorder(null);
        panel.getBpField().setBorder(null);

        JTextField geneNameField = panel.getVariantIdField();

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

        String snpWindowSizeText = panel.getBpField().getText();
        boolean validWindowSizeEntered = true; // must be both not empty and an int
        int snpWindowSize = 0;

        if(!snpWindowSizeText.isBlank()) {
            try {
                snpWindowSize = Integer.parseInt(snpWindowSizeText);
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

            convertVariantAndDownloadVcf(populations, snpList, snpWindowSize);

        } else {
            displayError(snpListInputted, snpFileImported, snpFileError, snpFileExtensionError,
                invalidCharacter, popSelected, validWindowSizeEntered);
        }
    }


    private void convertVariantAndDownloadVcf(ZoneSelection populations, List<String> variantList, int windowSize) {
        run(outFile -> {
            var hgVersion = Resource.config().getSelectedHumanGenome().toGRC();
            logger.log(Level.INFO,
                "Starting gene research using {0} HG version...", hgVersion);
            var download = frame.getBottomPanel().addState("Starting download", outFile);

            // Sets the locus building processus
            var variantConversion = new VariantConversion(variantList, hgVersion);
            download.setAssociatedProcessus(variantConversion);

            var notFound = new AtomicReference<>("");

            // Starts the processus and subscribes to its state
            variantConversion.start().doOnComplete(() -> {
                if ("".equals(notFound.get()) || ExceptionHandler.variantsNotFoundMessage(notFound.get())) {
                    var locusList = variantConversion.getResult();
                    if(windowSize!=0)
                        locusList = locusList.stream().map(l -> l.withWindow(windowSize)).toList();
                    downloadVcf(populations, outFile, locusList, download);
                } else {
                    download.cancel();
                    logger.log(Level.INFO, "Download to {0} cancelled", outFile.getName());
                }
            }).doOnError(e -> {
                logger.log(Level.WARNING, "Error while downloading or writing", e);
                download.error();
                ExceptionHandler.show(e);
            }).doOnNext(state -> {
                if (state.getAction() == State.States.CONFIRM_CONTINUE
                        && state.getObjectBeingProcessed() instanceof VariantsNotFoundException e) {
                    notFound.set(String.join(",", e.getNotFound()));
                } else if (state.getAction() == State.States.CANCELLED) {
                    logger.log(Level.INFO, "Download to {0} cancelled", outFile.getName());
                }
            }).subscribe(download::setState);
        });
    }


    private void displayError(boolean snpListInputted, boolean snpFileImported,
        boolean snpFileError, boolean snpFileExtensionError, boolean invalidCharacter,
        boolean popSelected, boolean validWindowSizeEntered) {

        var error = new Error(frame).append("run.fixerrors");

        var idSelector = panel.getVariantIdField();
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
