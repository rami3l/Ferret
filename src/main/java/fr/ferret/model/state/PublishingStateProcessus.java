package fr.ferret.model.state;

import fr.ferret.controller.exceptions.*;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

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
        checkStarted();
        state.next(new State(textElementBase, arg1, arg2));
    }

    protected void publishErrorAndCancel(Throwable error) {
        checkStarted();
        state.error(error);
        disposable.dispose();
    }

    protected void confirmContinue(Throwable error) {
        checkStarted();
        if(error instanceof GenesNotFoundException e) {
            state.next(new State("state.waiting", null, null));
            if(!ExceptionHandler.genesNotFoundMessage(e))
                cancel();
        }
    }

    public Flux<State> start() {
        Flux<State> stateFlux = Flux.create(s -> state = s);
        disposable = resultPromise.doOnSuccess(r -> {
            result = r;
            state.complete();
        }).subscribe();
        return stateFlux;
    }

    public void cancel() {
        checkStarted();
        disposable.dispose();
        state.error(new CancelledProcessusException());
    }

    public T getResult() {
        if(result == null)
            throw new IllegalStateException("Trying to get the result before the end of the processus");
        return result;
    }

    private void checkStarted() {
        if (state == null || disposable == null)
            throw new IllegalStateException("Cannot publish or cancel before starting processus");
    }

}
