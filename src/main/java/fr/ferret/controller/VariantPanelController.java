package fr.ferret.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import fr.ferret.model.utils.FileReader;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.VariantPanel;

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
        JCheckBox snpESPCheckBox = panel.getCheckbox();

        // Selected populations for the model
        var populations = getSelectedPopulations();
        boolean popSelected = !populations.isEmpty();

        // List which will contain the genes (from field or file)
        List<String> snpList = null;

        String snpString = geneNameField.getText();

        // Did the user input a list of gene
        boolean snpListInputted = snpString.length() > 0;

        // Did the user import a csv file
        String snpFileNameAndPath = panel.getFileSelector().getSelectedFile() == null ? null
                : panel.getFileSelector().getSelectedFile().getAbsolutePath();
        boolean snpFileImported = snpFileNameAndPath != null;

        // Are they errors in imported file (impossible to read, invalid extension or invalid
        // content)
        boolean snpFileError = false;
        boolean snpFileExtensionError = false;
        boolean invalidCharacter = false;
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

        if ((snpListInputted || (snpFileImported && !snpFileError && !snpFileExtensionError))
                && !invalidCharacter && validWindowSizeEntered && popSelected) {

            // TODO LINK WITH MODEL - see LocusPanelController to know how to deal with the file

        } else {
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
}
