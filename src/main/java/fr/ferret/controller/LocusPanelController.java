package fr.ferret.controller;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import fr.ferret.controller.settings.HumanGenomeVersions;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.LocusPanel;

/**
 * The {@link LocusPanel} controller
 */
public class LocusPanelController extends InputPanelController {

    private static final Logger LOG = Logger.getLogger(LocusPanelController.class.getName());

    private final LocusPanel locusPanel;

    public LocusPanelController(FerretFrame frame, LocusPanel locusPanel) {
        super(frame);
        this.locusPanel = locusPanel;
    }

    public void validateInfosAndRun(String fileNameAndPath) {
        // Reset borders
        locusPanel.getChromosomeList().setBorder(null);
        locusPanel.getInputStart().setBorder(null);
        locusPanel.getInputEnd().setBorder(null);

        // Selected populations for the model
        List<CharSequence> populations = getSelectedPopulations();
        boolean populationSelected = !populations.isEmpty();

        // Chr position input method
        String chrSelected = (String) locusPanel.getChromosomeList().getSelectedItem();
        boolean isChrSelected = !chrSelected.equals(" ");

        String startPosition = locusPanel.getInputStart().getText();
        String endPosition = locusPanel.getInputEnd().getText();

        boolean startSelected = !startPosition.isEmpty();
        boolean endSelected = !endPosition.isEmpty();
        boolean startEndValid = true, withinRange = true;
        int chrEndBound = 0;

        if (startSelected && endSelected) {
            int tempEndPos = -1, tempStartPos = -1;
            try {
                tempStartPos = Integer.parseInt(startPosition);
            } catch (NumberFormatException ex) {
                startSelected = false;
            }
            try {
                tempEndPos = Integer.parseInt(endPosition);
            } catch (NumberFormatException ex) {
                endSelected = false;
            }
            if (startSelected && endSelected) {
                startEndValid = (tempEndPos >= tempStartPos);
                if (startEndValid) {
                    Map<String, Integer> chrMap = new HashMap<>();
                    if (Resource.CONFIG.getSelectedHumanGenome() == HumanGenomeVersions.hg19) {
                        // Avoid too much if/else
                        chrMap.put("X", 155270560);
                        chrMap.put("1", 249250621);
                        chrMap.put("2", 243199373);
                        chrMap.put("3", 198022430);
                        chrMap.put("4", 191154276);
                        chrMap.put("5", 180915260);
                        chrMap.put("6", 171115067);
                        chrMap.put("7", 159138663);
                        chrMap.put("8", 146364022);
                        chrMap.put("9", 141213431);
                        chrMap.put("10", 135534747);
                        chrMap.put("11", 135006516);
                        chrMap.put("12", 133851895);
                        chrMap.put("13", 115169878);
                        chrMap.put("14", 107349540);
                        chrMap.put("15", 102531392);
                        chrMap.put("16", 90354753);
                        chrMap.put("17", 81195210);
                        chrMap.put("18", 78077248);
                        chrMap.put("19", 59128983);
                        chrMap.put("20", 63025520);
                        chrMap.put("21", 48129895);
                        chrMap.put("22", 51304566);

                        int validEnd = chrMap.get(chrSelected);
                        if (tempEndPos > validEnd || tempStartPos < 1) {
                            withinRange = false;
                            chrEndBound = validEnd;
                        }
                    } else {
                        chrMap.put("X", 156040895);
                        chrMap.put("1", 248956422);
                        chrMap.put("2", 242193529);
                        chrMap.put("3", 198295559);
                        chrMap.put("4", 190214555);
                        chrMap.put("5", 181538259);
                        chrMap.put("6", 170805979);
                        chrMap.put("7", 159345973);
                        chrMap.put("8", 145138636);
                        chrMap.put("9", 138394717);
                        chrMap.put("10", 133797422);
                        chrMap.put("11", 135086622);
                        chrMap.put("12", 133275309);
                        chrMap.put("13", 114364328);
                        chrMap.put("14", 107043718);
                        chrMap.put("15", 101991189);
                        chrMap.put("16", 90338345);
                        chrMap.put("17", 83257441);
                        chrMap.put("18", 80373285);
                        chrMap.put("19", 58617616);
                        chrMap.put("20", 64444167);
                        chrMap.put("21", 46709983);
                        chrMap.put("22", 50818468);

                        int validEnd = chrMap.get(chrSelected);
                        if (tempEndPos > validEnd || tempStartPos < 1) {
                            withinRange = false;
                            chrEndBound = validEnd;
                        }
                    }
                }
            }
        }

        // Valid input
        if (isChrSelected && populationSelected && startSelected && endSelected && startEndValid
                && withinRange) {
            LOG.log(Level.INFO, "Starting gene research...");
            // TODO LINK WITH MODEL

        } else { // Invalid input
            StringBuffer errorMessage = new StringBuffer(Resource.getTextElement("run.fixerrors"));
            if (!isChrSelected) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectchr"));
                locusPanel.getChromosomeList()
                        .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if (!populationSelected) {
                errorMessage.append("\n " + Resource.getTextElement("run.selectpop"));
                getFrame().getRegionPanel().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if (!startSelected) {
                errorMessage.append("\n " + Resource.getTextElement("run.startpos"));
                locusPanel.getInputStart().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if (!endSelected) {
                errorMessage.append("\n " + Resource.getTextElement("run.endpos"));
                locusPanel.getInputEnd().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if (!startEndValid) {
                errorMessage.append("\n " + Resource.getTextElement("run.invalidstart"));
                locusPanel.getInputStart().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                locusPanel.getInputEnd().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            if (!withinRange) {
                errorMessage.append("\n " + Resource.getTextElement("run.invalidpos.1") + " "
                        + chrSelected + " " + Resource.getTextElement("run.invalidpos.2") + " "
                        + chrEndBound);
                locusPanel.getInputStart().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                locusPanel.getInputEnd().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
            JOptionPane.showMessageDialog(getFrame(), errorMessage,
                    Resource.getTextElement("run.error"), JOptionPane.OK_OPTION);
        }
    }
}
