package fr.ferret.model.state;

import lombok.Getter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

/**
 * Extend this class if your class need to publish the state of its processus
 */
public abstract class PublishingStateProcessus<T> {

    @Getter
    private static final List<PublishingStateProcessus<?>> currentProcessusList = new ArrayList<>();

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
        state.tryEmitNext(State.confirmContinue(error));
    }

    /**
     * Starts the processus
     *
     * @return a {@link Flux} of {@link State states} you can subscribe to, to get the current state
     * of the processus
     */
    public Flux<State> start() {
        currentProcessusList.add(this);
        disposable = resultPromise.doOnSuccess(r -> {
            result = r;
            state.tryEmitComplete();
            currentProcessusList.remove(this);
        }).subscribe();
        return state.asFlux();
    }

    public void cancel() {
        currentProcessusList.remove(this);
        checkStarted();
        disposable.dispose();
        state.tryEmitNext(State.cancelled());
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
