package fr.ferret.controller;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
        String[] geneListArray = null;
        boolean geneListInputted = geneString.length() > 0;
        String geneFileNameAndPath = panel.getFileSelector().getSelectedFile() == null ? null
                : panel.getFileSelector().getSelectedFile().getAbsolutePath();
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
            if (geneFileNameAndPath.length() <= 4) {
                geneFileError = true;
            } else {
                String fileType = geneFileNameAndPath.substring(geneFileNameAndPath.length() - 4);
                var delimiter = switch (fileType) {
                    case ".csv" -> ",";
                    case ".tab", ".tsv" -> "\\t";
                    case ".txt" -> " ";
                    default -> {
                        geneFileExtensionError = true;
                        yield null;
                    }
                };

                ArrayList<String> geneListArrayList = new ArrayList<String>();

                if (delimiter != null) {
                    try (BufferedReader geneFileRead =
                            new BufferedReader(new FileReader(geneFileNameAndPath));) {
                        String geneStringToParse;
                        while ((geneStringToParse = geneFileRead.readLine()) != null) {
                            String[] text = geneStringToParse.split(delimiter);
                            for (int i = 0; i < text.length; i++) {
                                text[i] = text[i].replace(" ", "").toUpperCase(new Locale("all")); // remove
                                                                                                   // spaces
                                if (text[i].matches(invalidRegex)) { // identify invalid characters
                                    invalidCharacter = true;
                                    break;
                                }
                                if (text[i].length() > 0) {
                                    geneListArrayList.add(text[i]);
                                }
                            }
                        }
                        geneListArray =
                                geneListArrayList.toArray(new String[geneListArrayList.size()]);
                    } catch (IOException | NullPointerException e) {
                        // e.printStackTrace();
                        geneFileError = true;
                    } // File is empty

                }
            }

        } else if (geneListInputted) {
            geneString = geneString.toUpperCase(new Locale("all"));
            String geneList = geneString.replace(" ", "");
            invalidCharacter = geneList.replace(",", "").matches(invalidRegex);
            if (geneList.endsWith(",")) {
                geneList = geneList.substring(0, geneList.length() - 1);
            }
            geneListArray = geneList.split(",");
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
