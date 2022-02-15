package fr.ferret.controller;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.LocusPanel;

/**
 * The {@link LocusPanel} controller
 */
public class LocusPanelController extends InputPanelController {

    private static final Logger logger = Logger.getLogger(LocusPanelController.class.getName());

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
        var populations = getSelectedPopulations();
        boolean populationSelected = !populations.isEmpty();

        // Gets the selected chromosome
        String chrSelected = (String) locusPanel.getChromosomeList().getSelectedItem();
        boolean isChrSelected = !" ".equals(chrSelected);

        // Gets the selected start position
        String startPosition = locusPanel.getInputStart().getText();
        boolean startSelected = !startPosition.isEmpty();

        // Gets the selected end position
        String endPosition = locusPanel.getInputEnd().getText();
        boolean endSelected = !endPosition.isEmpty();

        boolean startEndValid = true;
        boolean withinRange = true;
        int chrEndBound = 0;

        if (startSelected && endSelected) {
            int startPos = -1;
            int endPos = -1;

            // Tries to get start position
            try {
                startPos = Integer.parseInt(startPosition);
            } catch (NumberFormatException ex) {
                startSelected = false;
            }

            // Tries to get end position
            try {
                endPos = Integer.parseInt(endPosition);
            } catch (NumberFormatException ex) {
                endSelected = false;
            }
            startEndValid = (endPos >= startPos);

            // Checks that given end position is not greater than chromosome end position
            if (startSelected && endSelected && startEndValid){
                int validEnd = Resource
                        .getChrEndPosition(Resource.CONFIG.getSelectedHumanGenome(), chrSelected)
                        .orElseGet(() -> {
                            logger.log(Level.WARNING, "Impossible to get chromosome end position."
                                + " Given end position may be invalid");
                            return Integer.MAX_VALUE;
                        });
                if (endPos > validEnd || startPos < 1) {
                    withinRange = false;
                    chrEndBound = validEnd;
                }
            }
        }

        // Valid input
        if (isChrSelected && populationSelected && startSelected && endSelected && startEndValid
                && withinRange) {
            logger.log(Level.INFO, "Starting gene research...");
            // TODO LINK WITH MODEL

        } else { // Invalid input
            displayError(isChrSelected, populationSelected, startSelected, endSelected,
                    startEndValid, withinRange, chrSelected, chrEndBound);

       }
    }

    private void displayError(boolean isChrSelected, boolean populationSelected,
            boolean startSelected, boolean endSelected, boolean startEndValid, boolean withinRange,
            String chrSelected, int chrEndBound) {
        var errorMessage = new StringBuilder(Resource.getTextElement("run.fixerrors"));
        if (!isChrSelected) {
            errorMessage.append("\n ").append(Resource.getTextElement("run.selectchr"));
            locusPanel.getChromosomeList()
                .setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        }
        if (!populationSelected) {
            errorMessage.append("\n ").append(Resource.getTextElement("run.selectpop"));
            getFrame().getRegionPanel().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        }
        if (!startSelected) {
            errorMessage.append("\n ").append(Resource.getTextElement("run.startpos"));
            locusPanel.getInputStart().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        }
        if (!endSelected) {
            errorMessage.append("\n ").append(Resource.getTextElement("run.endpos"));
            locusPanel.getInputEnd().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        }
        if (!startEndValid) {
            errorMessage.append("\n ").append(Resource.getTextElement("run.invalidstart"));
            locusPanel.getInputStart().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            locusPanel.getInputEnd().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        }
        if (!withinRange) {
            errorMessage.append("\n ").append(Resource.getTextElement("run.invalidpos.1"))
                .append(" ").append(chrSelected).append(" ")
                .append(Resource.getTextElement("run.invalidpos.2")).append(" ")
                .append(chrEndBound);
            locusPanel.getInputStart().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            locusPanel.getInputEnd().setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        }
        JOptionPane.showMessageDialog(getFrame(), errorMessage,
            Resource.getTextElement("run.error"), JOptionPane.ERROR_MESSAGE);
    }
}
