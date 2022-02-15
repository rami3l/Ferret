package fr.ferret.controller;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.VariantPanel;

/**
 * The {@link VariantPanel} controller
 */
public class VariantPanelController extends InputPanelController {

    private static final Logger logger = Logger.getLogger(VariantPanelController.class.getName());

    private final VariantPanel variantPanel;

    public VariantPanelController(FerretFrame frame, VariantPanel variantPanel) {
        super(frame);
        this.variantPanel = variantPanel;
    }

    public void validateInfosAndRun(String fileNameAndPath) {
        // Reset borders
        variantPanel.getVariantIdField().setBorder(null);
        variantPanel.getFileSelector().getRunButton().setBorder(null);
        variantPanel.getBpField().setBorder(null);

        // Traitement
        JTextField geneNameField = variantPanel.getVariantIdField();
        JCheckBox snpESPCheckBox = variantPanel.getCheckbox();


        // Selected populations for the model
        var populations = getSelectedPopulations();
        boolean popSelected = !populations.isEmpty();

        String snpString = geneNameField.getText();
        boolean snpListInputted = snpString.length() > 0;
        String snpFileNameAndPath = variantPanel.getFileSelector().getSelectedFile() == null ? null
                : variantPanel.getFileSelector().getSelectedFile().getAbsolutePath();
        boolean snpFileImported = snpFileNameAndPath != null;

        boolean snpFileError = false;
        boolean snpFileExtensionError = false;
        boolean invalidCharacter = false;
        String invalidRegex = ".*\\D.*"; // This is everything except numbers
        ArrayList<String> snpListArray = new ArrayList<String>();
        String snpWindowSize = variantPanel.getBpField().getText();
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
            if (snpFileNameAndPath.length() <= 4) {
                snpFileError = true;
            } else {
                String fileType = snpFileNameAndPath.substring(snpFileNameAndPath.length() - 4);
                String delimiter = null;
                switch (fileType) {
                    case ".csv":
                        delimiter = ",";
                        break;
                    case ".tab":
                    case ".tsv":
                        delimiter = "\\t";
                        break;
                    case ".txt":
                        delimiter = " ";
                        break;
                    default:
                        snpFileExtensionError = true;
                        break;
                }
                if (delimiter != null) {
                    try (BufferedReader snpFileRead =
                            new BufferedReader(new FileReader(snpFileNameAndPath));) {
                        String snpStringToParse;
                        while ((snpStringToParse = snpFileRead.readLine()) != null) {
                            String[] text = snpStringToParse.split(delimiter);
                            for (int i = 0; i < text.length; i++) {
                                text[i] = text[i].replace(" ", ""); // remove spaces
                                if (text[i].matches(invalidRegex)) { // identify invalid characters
                                    invalidCharacter = true; // probably can just throw error here,
                                                             // might be easier/more straight
                                                             // forward. But then errors wouldn't be
                                                             // 'accumulated' to the end
                                    break;
                                }
                                if (text[i].length() > 0) {
                                    snpListArray.add(text[i]);
                                }
                            }
                        }
                    } catch (IOException e) {
                        // e.printStackTrace();
                        snpFileError = true;
                    } catch (NullPointerException e) {
                        snpFileError = true;
                    }
                }
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
            snpListArray = new ArrayList<String>(Arrays.asList(text));
        }

        if ((snpListInputted || (snpFileImported && !snpFileError && !snpFileExtensionError))
                && !invalidCharacter && validWindowSizeEntered && popSelected) {

            logger.log(Level.INFO, "Starting gene research...");
            // TODO LINK WITH MODEL

            // this should be combined with the one single call to Ferret later

        } else {
            StringBuffer errorMessage = new StringBuffer("Correct the following errors:");
            if (!snpListInputted && !snpFileImported) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectvari"));
                variantPanel.getVariantIdField()
                        .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                variantPanel.getFileSelector().getRunButton()
                        .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if (snpFileImported && snpFileError) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectvari.ferr"));
                variantPanel.getFileSelector().getRunButton()
                        .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if (snpFileImported && snpFileExtensionError) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectvari.fext"));
                variantPanel.getFileSelector().getRunButton()
                        .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if ((snpListInputted || snpFileImported) && invalidCharacter) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectvari.cerr"));
                if (snpListInputted) {
                    variantPanel.getVariantIdField()
                            .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                } else {
                    variantPanel.getFileSelector().getRunButton()
                            .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                }
            }
            if (!popSelected) {
                errorMessage.append("\n ").append(Resource.getTextElement("run.selectpop"));
                getFrame().getRegionPanel().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if (!validWindowSizeEntered) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectvari.wsize"));
                variantPanel.getBpField().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            JOptionPane.showMessageDialog(getFrame(), errorMessage,
                    Resource.getTextElement("run.error"), JOptionPane.OK_OPTION);
        }
    }
}
