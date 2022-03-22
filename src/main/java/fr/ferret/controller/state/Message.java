package fr.ferret.controller.state;

import fr.ferret.model.locus.Locus;
import fr.ferret.model.state.State;
import fr.ferret.utils.Resource;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

@Getter
public class Message {

    /**
     * Contains the locations of the texts and tooltips for the state messages
     */
    private static final Map<State.States, String> stateTexts = new EnumMap<>(State.States.class);
    static {
        stateTexts.put(State.States.GENE_NAME_TO_ID, "state.geneNameToId");
        stateTexts.put(State.States.GENE_ID_TO_LOCUS, "state.geneIdToLocus");
        stateTexts.put(State.States.VARIANT_ID_TO_LOCUS, "state.variantIdToLocus");
        stateTexts.put(State.States.DOWNLOADING_HEADER, "state.downloadingHeader");
        stateTexts.put(State.States.DOWNLOADING_LINES, "state.downloadingLines");
        stateTexts.put(State.States.WRITING, "state.writingFile");
        stateTexts.put(State.States.WRITTEN, "state.fileWritten");
        stateTexts.put(State.States.CONFIRM_CONTINUE, "state.waiting");
        stateTexts.put(State.States.CANCELLED, "state.cancelled");
    }

    private final String text;
    private final String tooltip;

    /**
     * Creates a {@link Message} using the Resource Bundle for getting the text and the tooltip.<br>
     * <i>textElementBase</i>.text is used for the text, and <i>textElementBase.tooltip</i> for the
     * tooltip
     *
     * @param textElementBase The prefix tu use for getting the text and the tooltip
     * @param arg1            the object which will be used to format the text (for example %s will
     *                        be replaced by the object string representation in the text)
     * @param arg2            the object which will be used to format the tooltip. If null arg1 is
     *                        use instead
     */
    public Message(String textElementBase, Object arg1, Object arg2) {
        text = String.format(Resource.getTextElement(textElementBase + ".text"), arg1);
        tooltip = String.format(Resource.getTextElement(textElementBase + ".tooltip"), arg2);
    }

    /**
     * Creates a {@link Message} to display the {@link State} in the interface
     */
    public static Message from(State state) {
        var arg1 = state.getObjectBeingProcessed();
        var arg2 = state.getObjectBeingProcessed();
        if(state.getAction() == State.States.DOWNLOADING_LINES && arg1 instanceof Locus locus) {
            arg1 = locus.getChromosome();
        }
        return new Message(stateTexts.get(state.getAction()), arg1, arg2);
    }
}
