package fr.ferret.controller;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.google.common.io.Files;
import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import fr.ferret.controller.utils.FileUtils;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.GenePanel;

/**
 * The {@link GenePanel} controller
 */
public class GenePanelController extends InputPanelController<GenePanel> {

    private static final Logger logger = Logger.getLogger(GenePanelController.class.getName());

    public GenePanelController(FerretFrame frame, GenePanel genePanel) {
        super(frame, genePanel);
    }

    public void validateInfosAndRun(String fileNameAndPath) {
        // Reset borders
        panel.getInputField().setBorder(null);
        panel.getFileSelector().getRunButton().setBorder(null);

        // Traitement
        JTextField geneNameField = panel.getInputField();
        JRadioButton geneNameRadioButton = panel.getRdoName();
        JRadioButton geneNCBIRadioButton = panel.getRdoID();

        // Selected populations for the model
        var populations = getSelectedPopulations();
        boolean popSelected = !populations.isEmpty();

        String geneString = geneNameField.getText();
        List<String> geneList = new ArrayList<>();
        boolean geneListInputted = geneString.length() > 0;
        String geneFileNameAndPath = panel.getFileSelector().getSelectedFile() == null ?
            null :
            panel.getFileSelector().getSelectedFile().getAbsolutePath();
        boolean geneFileImported = geneFileNameAndPath != null;
        boolean geneFileError = false;
        boolean geneFileExtensionError = false;
        boolean invalidCharacter = false;
        boolean geneNameInputted = geneNameRadioButton.isSelected();
        // boolean fromNCBI = geneNCBIRadioButton.isSelected();

        var invalidRegex = geneNameInputted
            // This is everything except letters and numbers, including underscore
            ? ".*[^a-zA-Z0-9\\-].*"
            // This is everything except numbers
            : ".*\\D.*";


        if (geneFileImported) {

            try {
                geneList = FileUtils.readCsvLike(geneFileNameAndPath, invalidRegex);
            } catch (FileFormatException e) {
                geneFileExtensionError = true;
            } catch (FileContentException e) {
                invalidCharacter = true;
            } catch (IOException e) {
                geneFileError = true;
            }


        } else if (geneListInputted) {
            geneString = geneString.toUpperCase(new Locale("all"));
            String geneStringList = geneString.replace(" ", "");
            invalidCharacter = geneStringList.replace(",", "").matches(invalidRegex);
            if (geneStringList.endsWith(",")) {
                geneStringList = geneStringList.substring(0, geneStringList.length() - 1);
            }
            geneList = Arrays.stream(geneStringList.split(",")).toList();
        }

        // TODO: WUT IS THIS? (SHOULD PUT SAD PATH FIRST WITH EARLY EXIT, AND THEN HAPPY PATH)
        if ((geneListInputted || (geneFileImported && !geneFileError && !geneFileExtensionError))
                && !invalidCharacter && popSelected) {

            logger.log(Level.INFO, "Starting gene research...");
            // TODO LINK WITH MODEL

        } else {
            JComponent inputField = panel.getInputField();
            JComponent runButton = panel.getFileSelector().getRunButton();

            var error = new Error();

            if (!geneListInputted && !geneFileImported) {
                error.cr().append("run.selectgene").highlight(List.of(inputField, runButton));
            }
            if (geneFileImported && geneFileError) {
                error.cr().append("run.selectgene.ferr").highlight(List.of(runButton));
            }
            if (geneFileImported && geneFileExtensionError) {
                error.cr().append("run.selectgene.fext").highlight(List.of(runButton));
            }
            if (geneListInputted && invalidCharacter) {
                error.cr().append("run.selectgene.cerr").highlight(List.of(inputField));
            }
            if (geneFileImported && invalidCharacter) {
                error.cr().append("run.selectgene.cerr").highlight(List.of(runButton));
            }
            if (!popSelected) {
                error.cr().append("run.selectpop").highlight(List.of(frame.getRegionPanel()));
            }
            error.show();
        }
    }
}
