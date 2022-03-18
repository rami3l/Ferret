package fr.ferret.model.locus;

import com.pivovarit.function.ThrowingSupplier;
import fr.ferret.controller.exceptions.ConversionIncompleteException;
import fr.ferret.model.state.PublishingStateProcessus;
import fr.ferret.model.state.State;
import fr.ferret.model.utils.JsonDocument;
import fr.ferret.model.utils.VariantConverter;
import fr.ferret.utils.Resource;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VariantConversion extends PublishingStateProcessus<List<Locus>> {

    private static final Logger logger = Logger.getLogger(VariantConversion.class.getName());

    private static final String ID_URL_TEMPLATE = Resource.getServerConfig("ncbi.variant.idUrlTemplate");

    private static final int NB_RETRY = 3;
    private static final Duration RETRY_DELAY = Duration.ofMillis(500);

    private final List<String> variantsNotFound = new ArrayList<>();

    /**
     * The selected Human Genome version
     */
    private final String hgVersion;

    public VariantConversion(List<String> ids, String hgVersion) {
        this.hgVersion = hgVersion;
        // On ne garde que les ids/names qui ne sont pas vides (isBlank)
        var idsFlux = Flux.fromIterable(ids).filter(Predicate.not(String::isBlank));
        resultPromise = idsFlux.flatMap(this::fromId)
            .doOnError(this::publishErrorAndCancel)
            .doOnComplete(() -> {
                if(!variantsNotFound.isEmpty())
                    publishState(State.confirmContinue(new ConversionIncompleteException(variantsNotFound)));
            }).collectList();
    }

    private Mono<Locus> fromId(String id) {
        return Mono.defer(ThrowingSupplier.sneaky(() -> {
                publishState(State.variantIdToLocus(id));
                logger.log(Level.INFO, "Getting locus for variant of id : {0}", id);
                var json = new URL(String.format(ID_URL_TEMPLATE, id)).openStream();
            return VariantConverter.extractLocus(hgVersion, new JsonDocument(json)).switchIfEmpty(notFound(id));
        }))
            .retryWhen(Retry.backoff(NB_RETRY, RETRY_DELAY).filter(IOException.class::isInstance))
            .onErrorResume(Exceptions::isRetryExhausted, e -> Mono.error(e.getCause()))
            .doOnError(e -> logger.log(Level.WARNING,
                String.format("Error while getting locus for variant with id %s", id), e))
            .onErrorResume(e -> notFound(id));
    }

    private <T> Mono<T> notFound(String name) {
        return Mono.fromRunnable(() -> variantsNotFound.add(name));
    }
}
