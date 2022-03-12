package fr.ferret.model.state;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.exceptions.VcfStreamingException;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.net.UnknownHostException;
import java.nio.file.FileSystemException;

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

    protected <T> Mono<T> publishError(Throwable error) {
        try {
            throw error;
        } catch (UnknownHostException e) {
            ExceptionHandler.connectionError(e);
        } catch (VcfStreamingException e) {
            ExceptionHandler.vcfStreamingError(e);
        } catch (FileSystemException e) {
            ExceptionHandler.fileWritingError(e);
        } catch (Throwable e) {
            ExceptionHandler.unknownError(e);
        }
        if (state != null) {
            state.tryEmitError(error);
        }
        return Mono.empty();
    }

    protected void publishComplete() {
        if (state != null) {
            state.tryEmitComplete();
        }
    }

}
