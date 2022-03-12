package fr.ferret.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ferret.model.locus.Locus;
import fr.ferret.model.ZoneSelection;
import fr.ferret.model.state.StatePublisher;
import fr.ferret.model.vcf.VcfExport;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.LocusPanel;
import reactor.core.publisher.Flux;

/**
 * The {@link LocusPanel} controller
 */
public class LocusPanelController extends InputPanelController<LocusPanel> {

    private static final Logger logger = Logger.getLogger(LocusPanelController.class.getName());

    public LocusPanelController(FerretFrame frame) {
        super(frame, frame.getLocusPanel());
    }

    public void validateInfoAndRun() {
        // Reset borders
        panel.getChromosomeList().setBorder(null);
        panel.getInputStart().setBorder(null);
        panel.getInputEnd().setBorder(null);

        // Selected populations for the model
        var populations = getSelectedPopulations();
        boolean populationSelected = !populations.isEmpty();

        // Gets the selected chromosome
        String chrSelected = (String) panel.getChromosomeList().getSelectedItem();
        boolean isChrSelected = !" ".equals(chrSelected);

        // Gets the selected start position
        String startPosition = panel.getInputStart().getText();
        boolean startSelected = !startPosition.isEmpty();

        // Gets the selected end position
        String endPosition = panel.getInputEnd().getText();
        boolean endSelected = !endPosition.isEmpty();

        boolean startEndValid = true;
        boolean withinRange = true;
        int chrEndBound = 0;

        int startPos = -1;
        int endPos = -1;

        // Gets and check start and end positions
        if (startSelected && endSelected) {

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
            if (startSelected && endSelected && startEndValid) {
                int validEnd = Resource
                        .getChrEndPosition(Resource.config().getSelectedHumanGenome(), chrSelected)
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
            // Runs Ferret
            downloadVcf(populations, chrSelected, startPos, endPos);
        } else { // Invalid input
            displayError(isChrSelected, populationSelected, startSelected, endSelected,
                    startEndValid, withinRange, chrSelected, chrEndBound);
        }
    }

    private void downloadVcf(ZoneSelection populations, String chr, final int start,
            final int end) {
        run(outFile -> {
            logger.log(Level.INFO, "Starting gene research...");
            var download = frame.getBottomPanel().addState("Starting download", outFile);

            // Inits the vcf export processus, attaches it to the StatePublisher, and starts it
            var vcfProcessus = new VcfExport(Flux.just(new Locus(chr, start, end))).setFilter(populations);
            var statePublisher = new StatePublisher().attachTo(vcfProcessus);
            vcfProcessus.startTo(outFile);

            // Subscribes to the state of the launched processus via the StatePublisher
            statePublisher.getState().doOnComplete(download::complete).doOnError(e -> {
                        logger.log(Level.WARNING, "Error while downloading or writing");
                        download.error();
                    }).subscribe(download::setState);
        });
    }

    private void displayError(boolean isChrSelected, boolean populationSelected,
            boolean startSelected, boolean endSelected, boolean startEndValid, boolean withinRange,
            String chrSelected, int chrEndBound) {

        var startSelector = panel.getInputStart();
        var endSelector = panel.getInputEnd();

        var error = new Error(frame).append("run.fixerrors");

        if (!isChrSelected) {
            error.append("run.selectchr").highlight(panel.getChromosomeList());
        }
        if (!populationSelected) {
            error.append("run.selectpop").highlight(frame.getRegionPanel());
        }
        if (!startSelected) {
            error.append("run.startpos").highlight(startSelector);
        }
        if (!endSelected) {
            error.append("run.endpos").highlight(endSelector);
        }
        if (!startEndValid) {
            error.append("run.invalidstart").highlight(startSelector, endSelector);
        }
        if (!withinRange) {
            error.append("run.invalidpos", chrSelected, chrEndBound).highlight(startSelector,
                    endSelector);
        }
        error.show();
    }
}
