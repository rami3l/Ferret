package fr.ferret.model.state;

import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Sinks;

/**
 * Extend this class if your class need to publish the state of its processus
 */
public abstract class PublishingStateProcessus {

    private Sinks.Many<State> state;

    /**
     * Sets the {@link FluxSink} to publish the state of the processus to
     *
     * @param state the {@link FluxSink} to publish the state to
     * @return this {@link PublishingStateProcessus}
     */
    public void publishTo(Sinks.Many<State> state) {
        this.state = state;
    }

    /**
     * Publishes a {@link State} using the Resource Bundle for getting the text and the tooltip.<br>
     * _textElementBase_.text is used for the text, and _textElementBase_.tooltip for the tooltip
     *
     * @param textElementBase The prefix tu use for getting the text and the tooltip
     * @param arg1            the object which will be used to format the text (for example %s will
     *                        be replaced by the object string representation in the text)
     * @param arg2            the object which will be used to format the tooltip. If null arg1 is
     *                        use instead
     */
    protected void publishState(String textElementBase, Object arg1, Object arg2) {
        if (state != null) {
            state.tryEmitNext(new State(textElementBase, arg1, arg2));
        }
    }

    protected void publishError(Throwable e) {
        if (state != null) {
            state.tryEmitError(e);
        }
    }

    protected void publishComplete() {
        if (state != null) {
            state.tryEmitComplete();
        }
    }

    //protected abstract <T> T start();
}
