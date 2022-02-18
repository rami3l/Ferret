package fr.ferret.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import fr.ferret.controller.utils.FileUtils;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.VariantPanel;

/**
 * The {@link VariantPanel} controller
 */
public class VariantPanelController extends InputPanelController<VariantPanel> {

    private static final Logger logger = Logger.getLogger(VariantPanelController.class.getName());

    public VariantPanelController(FerretFrame frame, VariantPanel variantPanel) {
        super(frame, variantPanel);
    }

    public void validateInfosAndRun(String fileNameAndPath) {
        // Reset borders
        panel.getVariantIdField().setBorder(null);
        panel.getFileSelector().getRunButton().setBorder(null);
        panel.getBpField().setBorder(null);

        // Traitement
        JTextField geneNameField = panel.getVariantIdField();
        JCheckBox snpESPCheckBox = panel.getCheckbox();


        // Selected populations for the model
        var populations = getSelectedPopulations();
        boolean popSelected = !populations.isEmpty();

        String snpString = geneNameField.getText();
        boolean snpListInputted = snpString.length() > 0;
        String snpFileNameAndPath = panel.getFileSelector().getSelectedFile() == null ? null
                : panel.getFileSelector().getSelectedFile().getAbsolutePath();
        boolean snpFileImported = snpFileNameAndPath != null;

        boolean snpFileError = false;
        boolean snpFileExtensionError = false;
        boolean invalidCharacter = false;
        String invalidRegex = ".*\\D.*"; // This is everything except numbers
        List<String> snpList = new ArrayList<>();
        String snpWindowSize = panel.getBpField().getText();
        boolean snpWindowSelected = snpESPCheckBox.isSelected();
        boolean validWindowSizeEntered = true; // must be both not empty and an int

        if (snpWindowSelected) {
            if (snpWindowSize.length() == 0) {
                validWindowSizeEntered = false; // must have something there
            } else { // test for non ints
                for (int i = 0; i < snpWindowSize.length(); i++) {
                    if (!Character.isDigit(snpWindowSize.charAt(i))) {
                        validWindowSizeEntered = false;
                    }
                }
            }
        } else { // if no window specified, it's always fine
            snpWindowSize = "0";
        }

        if (snpFileImported) {

            try {
                snpList = FileUtils.readCsvLike(snpFileNameAndPath, invalidRegex);
            } catch (FileFormatException e) {
                snpFileExtensionError = true;
            } catch (FileContentException e) {
                invalidCharacter = true;
            } catch (IOException e) {
                snpFileError = true;
            }

        } else if (snpListInputted) {

            while (snpString.endsWith(",") || snpString.endsWith(" ")) { // maybe this should be
                                                                         // added for gene input too
                snpString = snpString.substring(0, snpString.length() - 1);
            }
            String[] text = snpString.split(",");
            for (int i = 0; i < text.length; i++) {
                text[i] = text[i].replace(" ", "");// remove spaces
                if (text[i].matches(invalidRegex)) {
                    invalidCharacter = true;
                    break;
                }
            }
            snpList = Arrays.asList(text);
        }

        if ((snpListInputted || (snpFileImported && !snpFileError && !snpFileExtensionError))
                && !invalidCharacter && validWindowSizeEntered && popSelected) {

            logger.log(Level.INFO, "Starting gene research...");
            // TODO LINK WITH MODEL

            // this should be combined with the one single call to Ferret later

        } else {
            var error = new Error();

            var idSelector = panel.getVariantIdField();
            var runButton = panel.getFileSelector().getRunButton();

            if (!snpListInputted && !snpFileImported) {
                error.append("run.selectvari").highlight(List.of(idSelector, runButton));
            }
            if (snpFileImported && snpFileError) {
                error.append("run.selectvari.ferr").highlight(List.of(runButton));
            }
            if (snpFileImported && snpFileExtensionError) {
                error.append("run.selectvari.fext").highlight(List.of(runButton));
            }
            if ((snpListInputted || snpFileImported) && invalidCharacter) {
                error.append("run.selectvari.cerr").highlight(List.of(snpListInputted ? idSelector : runButton));
            }
            if (!popSelected) {
                error.append("run.selectpop").highlight(List.of(frame.getRegionPanel()));
            }
            if (!validWindowSizeEntered) {
                error.append("run.selectvari.wsize").highlight(List.of(panel.getBpField()));
            }
            error.show();
        }
    }
}
