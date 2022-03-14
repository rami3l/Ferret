package fr.ferret.model.state;

import fr.ferret.controller.exceptions.*;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.net.UnknownHostException;
import java.nio.file.FileSystemException;

/**
 * Extend this class if your class need to publish the state of its processus
 */
public abstract class PublishingStateProcessus<T> {

    // TODO: add a warning while trying to close Ferret although an export is not finished -> keep somewhere the list of VcfExports

    private FluxSink<State> state;
    private Disposable disposable;
    protected Mono<T> resultPromise;
    private T result;

    /**
     * Publishes a {@link State} created with the passed parameters. See the
     * {@link State#State javadoc} of the State constructor for more information on parameters
     */
    protected void publishState(String textElementBase, Object arg1, Object arg2) {
        if (state != null) {
            state.next(new State(textElementBase, arg1, arg2));
        }
    }

    protected <R> Mono<R> publishError(Throwable error) {
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
            state.error(error);
        }
        return Mono.empty();
    }

    protected void publishWarning(Throwable error) {
        if(error instanceof GenesNotFoundException e) {
            state.next(new State("state.waiting", null, null));
            ExceptionHandler.genesNotFoundError(e);
        }
    }

    protected void publishComplete() {
        if (state != null) {
            state.complete();
        }
    }

    public Flux<State> start() {
        disposable = resultPromise.subscribe(r -> {
            result = r;
            publishComplete();
        });
        return Flux.create(s -> state = s);
    }

    public void cancel() {
        if(disposable == null)
            return;
        disposable.dispose();
        state.error(new CancelledProcessusException());
    }

    public T getResult() {
        if(result == null)
            throw new IllegalStateException("Trying to get the result before the end of the processus");
        return result;
    }

}
