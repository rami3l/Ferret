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
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.GenePanel;
import fr.ferret.view.utils.Resource;

/**
 * The {@link GenePanel} controller
 */
public class GenePanelController extends InputPanelController {

    private static final Logger LOG = Logger.getLogger(GenePanelController.class.getName());

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
                    case ".tab":
                    case ".tsv":
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

            LOG.log(Level.INFO, "Starting gene research...");
            // TODO LINK WITH MODEL

            // this should be combined with the one single call to Ferret later
            /*
             * final Integer[] variants = {0}; String output = null;
             * 
             * switch (currFileOut[0]) { case ALL: output = "all"; break; case FRQ: output = "freq";
             * break; case VCF: output = "vcf"; break; }
             * 
             * String webAddress = null;
             * 
             * 
             * if (currVersion[0] == version1KG.ONE) { webAddress =
             * "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20110521/ALL.chr$.phase1_release_v3.20101123.snps_indels_svs.genotypes.vcf.gz";
             * } else if (currVersion[0] == version1KG.THREE & defaultHG[0]) { webAddress =
             * "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/ALL.chr$.phase3_shapeit2_mvncall_integrated_v5a.20130502.genotypes.vcf.gz";
             * } else if (currVersion[0] == version1KG.THREE & !defaultHG[0]) { webAddress =
             * "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/supporting/GRCh38_positions/ALL.chr$.phase3_shapeit2_mvncall_integrated_v3plus_nounphased.rsID.genotypes.GRCh38_dbSNP_no_SVs.vcf.gz";
             * }
             * 
             * String geneQueryType; if (geneNameInputted) { geneQueryType = "geneName"; } else {
             * geneQueryType = "geneID"; }
             * 
             * final FerretData currFerretWorker = new FerretData(geneQueryType, geneListArray,
             * populations, fileNameAndPath, getESP, progressText, webAddress, mafThreshold[0],
             * ESPMAFBoolean[0], output, defaultHG[0]);
             * 
             * currFerretWorker.addPropertyChangeListener(new PropertyChangeListener() {
             * 
             * @Override public void propertyChange(PropertyChangeEvent evt) { switch
             * (evt.getPropertyName()) { case "progress": progressBar.setValue((Integer)
             * evt.getNewValue()); case "state": try { switch ((StateValue) evt.getNewValue()) {
             * case DONE: progressWindow.setVisible(false); try { variants[0] =
             * currFerretWorker.get(); } catch (ExecutionException e) { e.printStackTrace();
             * variants[0] = -1; } catch (InterruptedException e) { e.printStackTrace(); variants[0]
             * = -1; }
             * 
             * new File("evsClient0_15.jar").delete(); Object[] options = {"Yes", "No"}; int choice;
             * System.out.println("Total Time: " + (System.nanoTime() - startTime)); if (variants[0]
             * == 1) { choice = JOptionPane.showOptionDialog(SNPFerret,
             * "Files have been downloaded\nDo you want to close Ferret?", "Close Ferret?",
             * JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null); } else if
             * (variants[0] == -3) { choice = JOptionPane.showOptionDialog(SNPFerret,
             * "After applying the MAF threshold, no variants were found" +
             * "\nDo you want to close Ferret?", "Close Ferret?", JOptionPane.YES_NO_OPTION,
             * JOptionPane.PLAIN_MESSAGE, null, options, null); } else if (variants[0] == 0) {
             * choice = JOptionPane.showOptionDialog(SNPFerret,
             * "No variants were found in this region\nDo you want to close Ferret?",
             * "Close Ferret?", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
             * null); } else if (variants[0] == -1) { choice =
             * JOptionPane.showOptionDialog(SNPFerret,
             * "Ferret has encountered a problem downloading data. \n" +
             * "Please try again later or consult the FAQ. \nDo you want to close Ferret?",
             * "Close Ferret?", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
             * null); } else { choice = JOptionPane.NO_OPTION; } if (choice ==
             * JOptionPane.YES_OPTION) { SNPFerret.dispose(); System.exit(0); } else {
             * enableComponents(SNPFerret, true); if (currFileOut[0] == fileOutput.VCF) {
             * snpESPCheckBox.setEnabled(false); geneESPCheckBox.setEnabled(false);
             * chrESPCheckBox.setEnabled(false); } for (int i = 0; i < asnsub.length; i++) {
             * asnPanel.add(asnsub[i]); if (asnsub[i].getText().contains("n=0")) {
             * asnsub[i].setEnabled(false); } } progressText.setText("Initializing...");
             * progressBar.setValue(0); checkBoxReset(); } break; case STARTED: case PENDING:
             * Dimension windowSize = SNPFerret.getSize(); progressWindow.setSize(new
             * Dimension((int) (windowSize.width * .5), (int) (windowSize.height * .2)));
             * progressWindow.setLocationRelativeTo(SNPFerret); progressWindow.setVisible(true);
             * enableComponents(SNPFerret, false); } } catch (ClassCastException e) { } }
             * 
             * } }); currFerretWorker.execute();
             */
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
