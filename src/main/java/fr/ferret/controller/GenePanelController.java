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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link GenePanel} controller
 */
public class GenePanelController extends InputPanelController<GenePanel> {

    private static final Logger logger = Logger.getLogger(GenePanelController.class.getName());

    public GenePanelController(FerretFrame frame, GenePanel genePanel) {
        super(frame, genePanel);
    }

    public void validateInfosAndRun(String fileNameAndPath) {
        // Reset the borders
        panel.getInputField().setBorder(null);
        panel.getFileSelector().getRunButton().setBorder(null);

        JTextField geneNameField = panel.getInputField();
        JRadioButton geneNameRadioButton = panel.getRdoName();

        // Selected populations for the model
        var populations = getSelectedPopulations();
        boolean popSelected = !populations.isEmpty();

        // List which will contain the genes (from field or file)
        List<String> geneList = null;

        String geneString = geneNameField.getText();

        // Did the user input a list of gene
        boolean geneListInputted = geneString.length() > 0;
        // Names or ids inputted ?
        boolean geneNameInputted = geneNameRadioButton.isSelected();

        // Did the user import a csv file
        String geneFileNameAndPath = panel.getFileSelector().getSelectedFile() == null ? null
                : panel.getFileSelector().getSelectedFile().getAbsolutePath();
        boolean geneFileImported = geneFileNameAndPath != null;

        // Are they errors in imported file (impossible to read, invalid extension or invalid content)
        boolean geneFileError = false;
        boolean geneFileExtensionError = false;
        boolean invalidCharacter = false;

        // invalid characters for the genes (inputted as a list or a file)
        var invalidRegex = geneNameInputted
            // This is everything except letters and numbers, including underscore
            ? ".*[^a-zA-Z0-9\\-].*"
            // This is everything except numbers
            : ".*\\D.*";


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

        // TODO: WUT IS THIS? (SHOULD PUT SAD PATH FIRST WITH EARLY EXIT, AND THEN HAPPY PATH)
        if ((geneListInputted || (geneFileImported && !geneFileError && !geneFileExtensionError))
                && !invalidCharacter && popSelected) {

            // TODO: What is locale "all" ? Locale.ROOT ?
            var locale = new Locale("all");
            geneList = geneList.stream().map(text -> text.toUpperCase(locale)).toList();

            logger.log(Level.INFO, "Starting gene research...");
            // TODO LINK WITH MODEL

        } else {
            JComponent inputField = panel.getInputField();
            JComponent runButton = panel.getFileSelector().getRunButton();

            var error = new Error();

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
}
