package fr.ferret.model.state;

import fr.ferret.model.locus.Locus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * An object representing the state of a processus (a download for example). It contains a text and
 * a tooltip. It can be used to set the state of a {@link fr.ferret.view.panel.StatePanel state
 * panel} in the {@link fr.ferret.view.panel.BottomPanel bottom panel}, using the <i>setState</i>
 * method.
 */
@Getter
@AllArgsConstructor
public final class State {

    public enum States {
        DOWNLOADING_HEADER, DOWNLOADING_LINES, GENE_ID_TO_LOCUS, GENE_NAME_TO_ID, WRITING, WRITTEN,
        CONFIRM_CONTINUE
    }

    private final States action;
    private final Object objectBeingProcessed;

    public static State geneNameToId(String name) {
        return new State(States.GENE_NAME_TO_ID, name);
    }

    public static State geneIdToLocus(String ids) {
        return new State(States.GENE_ID_TO_LOCUS, ids);
    }

    public static State writing(String filename) {
        return new State(States.WRITING, filename);
    }

    public static State written(String filename) {
        return new State(States.WRITTEN, filename);
    }

    public static State downloadingLines(Locus locus) {
        return new State(States.DOWNLOADING_LINES, locus);
    }

    public static State downloadingHeader(String chromosome) {
        return new State(States.DOWNLOADING_HEADER, chromosome);
    }

    public static State confirmContinue(List<String> genesNotFound) {
        return new State(States.CONFIRM_CONTINUE, genesNotFound);
    }
}
