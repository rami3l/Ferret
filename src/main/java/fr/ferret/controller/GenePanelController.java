package fr.ferret.controller;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import fr.ferret.controller.exceptions.GenesNotFoundException;
import fr.ferret.controller.state.Error;
import fr.ferret.model.ZoneSelection;
import fr.ferret.model.locus.Locus;
import fr.ferret.model.locus.GeneConversion;
import fr.ferret.model.state.State;
import fr.ferret.model.utils.FileReader;
import fr.ferret.model.vcf.VcfExport;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.StatePanel;
import fr.ferret.view.panel.inputs.GenePanel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
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

            convertGenesAndDownloadVcf(populations, geneList);

        } else {
            displayError(geneListInputted, geneFileImported, geneFileError, geneFileExtensionError,
                    invalidCharacter, popSelected);
        }
    }

    private void convertGenesAndDownloadVcf(ZoneSelection populations, List<String> geneList) {
        run(outFile -> {
            var assemblyAccVer = Resource.getAssemblyAccessVersion();
            logger.log(Level.INFO, "Starting gene research using {0} assembly accession version...", assemblyAccVer);
            var download = frame.getBottomPanel().addState("Starting download", outFile);

            // Sets the locus building processus
            var geneConversion = new GeneConversion(geneList, assemblyAccVer);
            download.setAssociatedProcessus(geneConversion);

            var notFound = new AtomicReference<>("");

            // Starts the processus and subscribes to its state
            geneConversion.start()
                .doOnComplete(
                    () -> {
                        if("".equals(notFound.get()) || ExceptionHandler.genesNotFoundMessage(notFound.get())) {
                            downloadVcf(populations, outFile, geneConversion.getResult(), download);
                        } else {
                            download.cancel();
                            logger.log(Level.INFO, "Download to {0} cancelled", outFile.getName());
                        }
                    }
                ).doOnError(e -> {
                    logger.log(Level.WARNING, "Error while downloading or writing", e);
                    download.error();
                    ExceptionHandler.show(e);
                }).doOnNext(state -> {
                    if(state.getAction()== State.States.CONFIRM_CONTINUE
                            && state.getObjectBeingProcessed() instanceof GenesNotFoundException e) {
                        notFound.set(String.join(",", e.getNotFound()));
                    } else if(state.getAction() == State.States.CANCELLED) {
                        logger.log(Level.INFO, "Download to {0} cancelled", outFile.getName());
                    }
                })
                .subscribe(download::setState);
        });
    }


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
