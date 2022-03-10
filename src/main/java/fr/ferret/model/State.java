package fr.ferret.model;

import fr.ferret.utils.Resource;
import lombok.Getter;

/**
 * An object representing the state of a processus (a download for example). It contains a text and
 * a tooltip. It can be used to set the state of a {@link fr.ferret.view.panel.StatePanel state
 * panel} in the {@link fr.ferret.view.panel.BottomPanel bottom panel}, using the <i>setState</i>
 * method.
 */
@Getter public final class State {
    public static final String DOWNLOADING_HEADER = "state.downloadingHeader";
    public static final String DOWNLOADING_LINES = "state.downloadingLines";
    public static final String WRITING = "state.writingFile";
    public static final String WRITTEN = "state.fileWritten";

    private final String text;
    private final String tooltip;

    /**
     * Creates a {@link State} using the Resource Bundle for getting the text and the tooltip.<br>
     * _textElementBase_.text is used for the text, and _textElementBase_.tooltip for the tooltip
     *
     * @param textElementBase The prefix tu use for getting the text and the tooltip
     * @param arg1            the object which will be used to format the text (for example %s will
     *                        be replaced by the object string representation in the text)
     * @param arg2            the object which will be used to format the tooltip. If null arg1 is
     *                        use instead
     */
    public State(String textElementBase, Object arg1, Object arg2) {
        if (arg2 == null)
            arg2 = arg1;
        text = String.format(Resource.getTextElement(textElementBase + ".text"), arg1);
        tooltip = String.format(Resource.getTextElement(textElementBase + ".tooltip"), arg2);
    }
}
