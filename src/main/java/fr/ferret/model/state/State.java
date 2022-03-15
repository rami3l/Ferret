package fr.ferret.model.state;

import fr.ferret.model.locus.Locus;
import fr.ferret.utils.Resource;
import lombok.Getter;

/**
 * An object representing the state of a processus (a download for example). It contains a text and
 * a tooltip. It can be used to set the state of a {@link fr.ferret.view.panel.StatePanel state
 * panel} in the {@link fr.ferret.view.panel.BottomPanel bottom panel}, using the <i>setState</i>
 * method.
 */
@Getter public final class State {
    private static final String GENE_NAME_TO_ID = "state.geneNameToId";
    private static final String GENE_ID_TO_LOCUS = "state.geneIdToLocus";
    private static final String DOWNLOADING_HEADER = "state.downloadingHeader";
    private static final String DOWNLOADING_LINES = "state.downloadingLines";
    private static final String WRITING = "state.writingFile";
    private static final String WRITTEN = "state.fileWritten";

    private final String text;
    private final String tooltip;

    /**
     * Creates a {@link State} using the Resource Bundle for getting the text and the tooltip.<br>
     * <i>textElementBase</i>.text is used for the text, and <i>textElementBase.tooltip</i> for the
     * tooltip
     *
     * @param textElementBase The prefix tu use for getting the text and the tooltip
     * @param arg1            the object which will be used to format the text (for example %s will
     *                        be replaced by the object string representation in the text)
     * @param arg2            the object which will be used to format the tooltip. If null arg1 is
     *                        use instead
     */
    public State(String textElementBase, Object arg1, Object arg2) {
        text = String.format(Resource.getTextElement(textElementBase + ".text"), arg1);
        tooltip = String.format(Resource.getTextElement(textElementBase + ".tooltip"), arg2);
    }

    public static State geneNameToId(String name) {
        return new State(GENE_NAME_TO_ID, name, name);
    }

    public static State geneIdToLocus(String ids) {
        return new State(GENE_ID_TO_LOCUS, ids, ids);
    }

    public static State writing(String filename) {
        return new State(WRITING, filename, filename);
    }

    public static State written(String filename) {
        return new State(WRITTEN, filename, filename);
    }

    public static State downloadingLines(Locus locus) {
        return new State(DOWNLOADING_LINES, locus.getChromosome(), locus);
    }

    public static State downloadingHeader(String chromosome) {
        return new State(DOWNLOADING_HEADER, chromosome, chromosome);
    }
}
