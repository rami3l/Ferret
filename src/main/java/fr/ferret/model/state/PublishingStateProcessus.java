package fr.ferret.model.state;

import fr.ferret.controller.exceptions.*;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * Extend this class if your class need to publish the state of its processus
 */
public abstract class PublishingStateProcessus<T> {

    // TODO: add a warning while trying to close Ferret although an export is not finished -> keep somewhere the list of VcfExports

    private final Sinks.Many<State> state = Sinks.many().multicast().directBestEffort();
    private Disposable disposable;
    protected Mono<T> resultPromise;
    private T result;

    /**
     * Publishes a {@link State} created with the passed parameters. See the
     * {@link State#State javadoc} of the State constructor for more information on parameters
     */
    protected void publishState(State state) {
        this.state.tryEmitNext(state);
    }

    protected void publishErrorAndCancel(Throwable error) {
        checkStarted();
        state.tryEmitError(error);
        disposable.dispose();
    }

    protected void confirmContinue(Throwable error) {
        // TODO: find a solution to not have to filter exceptions type here
        if(error instanceof GenesNotFoundException e) {
            state.tryEmitNext(State.confirmContinue(e.getNotFound()));
        }
    }

    /**
     * Starts the processus
     *
     * @return a {@link Flux} of {@link State states} you can subscribe to, to get the current state
     * of the processus
     */
    public Flux<State> start() {
        disposable = resultPromise.doOnSuccess(r -> {
            result = r;
            state.tryEmitComplete();
        }).subscribe();
        return state.asFlux();
    }

    public void cancel() {
        checkStarted();
        disposable.dispose();
        state.tryEmitError(new CancelledProcessusException());
    }

    public T getResult() {
        if(result == null)
            throw new IllegalStateException("Trying to get the result before the end of the processus");
        return result;
    }

    private void checkStarted() {
        if (disposable == null)
            throw new IllegalStateException("Cannot cancel before starting the processus");
    }

}
