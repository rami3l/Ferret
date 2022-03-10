package fr.ferret.model.state;

import fr.ferret.model.locus.Locus;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Sinks;

public class StatePublisher {

    private Sinks.Many<State> state;

    public StatePublisher() {
        state = Sinks.many().multicast().directBestEffort();
    }

    /**
     * Attaches a {@link PublishingStateProcessus} so that it can publish its state. You can attach
     * multiple processus to the same {@link StatePublisher} if they happen consecutively and are
     * part of the same global processus (For example getting the {@link Locus} of genes, then
     * downloading and processing the VCF containing these {@link Locus})
     *
     * @param processus The {@link PublishingStateProcessus} to attach to
     * @return this {@link StatePublisher}
     */
    public StatePublisher attachTo(PublishingStateProcessus processus) {
        processus.publishTo(state);
        return this;
    }

    /**
     * Returns the state of the attached processus so that you can subscribe to it
     */
    public Flux<State> getState() {
        return state.asFlux();
    }
}
