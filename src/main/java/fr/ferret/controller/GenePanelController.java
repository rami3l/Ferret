package fr.ferret.controller;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.GenePanel;

/**
 * The {@link GenePanel} controller
 */
public class GenePanelController extends InputPanelController {

    private static final Logger logger = Logger.getLogger(GenePanelController.class.getName());

    private final GenePanel genePanel;

    public GenePanelController(FerretFrame frame, GenePanel genePanel) {
        super(frame);
        this.genePanel = genePanel;
    }

    public void validateInfosAndRun(String fileNameAndPath) {
        // Reset borders
        genePanel.getInputField().setBorder(null);
        genePanel.getFileSelector().getRunButton().setBorder(null);

        // Traitement
        JTextField geneNameField = genePanel.getInputField();
        JRadioButton geneNameRadioButton = genePanel.getRdoName();
        JRadioButton geneNCBIRadioButton = genePanel.getRdoID();

        // Selected populations for the model
        List<CharSequence> populations = getSelectedPopulations();
        boolean popSelected = !populations.isEmpty();

        String geneString = geneNameField.getText();
        String[] geneListArray = null;
        boolean geneListInputted = geneString.length() > 0;
        String geneFileNameAndPath = genePanel.getFileSelector().getSelectedFile() == null ? null
                : genePanel.getFileSelector().getSelectedFile().getAbsolutePath();
        boolean geneFileImported = geneFileNameAndPath != null;
        boolean geneFileError = false;
        boolean geneFileExtensionError = false;
        boolean invalidCharacter = false;
        boolean geneNameInputted = geneNameRadioButton.isSelected();
        // boolean fromNCBI = geneNCBIRadioButton.isSelected();

        String invalidRegex;
        if (geneNameInputted) {
            invalidRegex = ".*[^a-zA-Z0-9\\-].*"; // This is everything except letters and numbers,
                                                  // including underscore
        } else {
            invalidRegex = ".*\\D.*"; // This is everything except numbers
        }

        if (geneFileImported) {
            if (geneFileNameAndPath.length() <= 4) {
                geneFileError = true;
            } else {
                String fileType = geneFileNameAndPath.substring(geneFileNameAndPath.length() - 4);
                String delimiter = null;
                switch (fileType) {
                    case ".csv":
                        delimiter = ",";
                        break;
                    case ".tab", ".tsv":
                        delimiter = "\\t";
                        break;
                    case ".txt":
                        delimiter = " ";
                        break;
                    default:
                        geneFileExtensionError = true;
                        break;
                }
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
                    } catch (IOException e) {
                        // e.printStackTrace();
                        geneFileError = true;
                    } catch (NullPointerException e) {
                        // File is empty
                        geneFileError = true;
                    }
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

        if ((geneListInputted || (geneFileImported && !geneFileError && !geneFileExtensionError))
                && !invalidCharacter && popSelected) {

            logger.log(Level.INFO, "Starting gene research...");
            // TODO LINK WITH MODEL

        } else {
            StringBuffer errorMessage = new StringBuffer("Correct the following errors:");
            if (!geneListInputted && !geneFileImported) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectgene"));
                genePanel.getInputField().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                genePanel.getFileSelector().getRunButton()
                        .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if (geneFileImported && geneFileError) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectgene.ferr"));
                genePanel.getFileSelector().getRunButton()
                        .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if (geneFileImported && geneFileExtensionError) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectgene.fext"));
                genePanel.getFileSelector().getRunButton()
                        .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if ((geneListInputted || geneFileImported) && invalidCharacter) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectgene.cerr"));
                if (geneListInputted) {
                    genePanel.getInputField()
                            .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                } else {
                    genePanel.getFileSelector().getRunButton()
                            .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                }
            }
            if (!popSelected) {
                errorMessage.append("\n ").append(Resource.getTextElement("run.selectpop"));
                getFrame().getRegionPanel().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            JOptionPane.showMessageDialog(getFrame(), errorMessage,
                    Resource.getTextElement("run.error"), JOptionPane.OK_OPTION);
        }
    }
}
