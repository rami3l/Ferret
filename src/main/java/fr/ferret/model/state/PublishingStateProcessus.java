package fr.ferret.model.state;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.exceptions.GenesNotFoundException;
import fr.ferret.controller.exceptions.NoIdFoundException;
import fr.ferret.controller.exceptions.VcfStreamingException;
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
     * Sets the {@link Sinks.Many 'skink'} to publish the state of the processus to
     *
     * @param state the {@link Sinks.Many 'sink'} to publish the state to
     */
    public void publishTo(Sinks.Many<State> state) {
        this.state = state;
    }

    /**
     * Publishes a {@link State} created with the passed parameters. See the
     * {@link State#State javadoc} of the State constructor for more information on parameters
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
        } catch (NoIdFoundException e) {
            ExceptionHandler.noIdFoundError(e);
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

    protected void publishWarning(Throwable error) {
        if(error instanceof GenesNotFoundException e) {
            if(state != null){
                state.tryEmitNext(new State("state.waiting", null, null));
            }
            ExceptionHandler.genesNotFoundError(e);
        }
    }

    protected void publishComplete() {
        if (state != null) {
            state.tryEmitComplete();
        }
    }

}
