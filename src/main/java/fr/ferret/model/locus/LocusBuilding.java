package fr.ferret.model.locus;

import com.pivovarit.function.ThrowingSupplier;
import fr.ferret.controller.exceptions.GenesNotFoundException;
import fr.ferret.controller.exceptions.NoIdFoundException;
import fr.ferret.model.state.State;
import fr.ferret.model.state.PublishingStateProcessus;
import fr.ferret.model.utils.GeneConverter;
import fr.ferret.model.utils.JsonDocument;
import fr.ferret.utils.Conversion;
import fr.ferret.utils.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LocusBuilding extends PublishingStateProcessus<List<Locus>> {

    private static final Logger logger = Logger.getLogger(LocusBuilding.class.getName());

    private static final String ID_URL_TEMPLATE = Resource.getServerConfig("ncbi.idUrlTemplate");
    private static final String NAME_URL_TEMPLATE = Resource.getServerConfig("ncbi.nameUrlTemplate");

    /** Delay between requests, to avoid getting response code 429 from ncbi server */
    private static final Duration DELAY = Duration.ofMillis(200);

    private static final int NB_RETRY = 3;
    private static final Duration RETRY_DELAY = Duration.ofMillis(500);

    /**
     * The selected <i>assembly accession version</i>
     */
    private final String assemblyAccVer;

    private final List<String> genesNotFound = new ArrayList<>();

    /**
     * @param assemblyAccVer The <i>assembly access version</i> to use for getting start and end positions
     */
    public LocusBuilding(String assemblyAccVer) {
        this.assemblyAccVer = assemblyAccVer;
    }

    /**
     * Converts the found gene names/ids to locus.<br>
     *
     * @param idsOrNames A {@link List list} of gene names/ids
     * @return A {@link Flux} containing the locus for found genes (empty in case of error).
     */
    public LocusBuilding fromGenes(List<String> idsOrNames) {
        var flux = Flux.fromIterable(idsOrNames).filter(Predicate.not(String::isBlank));
        // ids are numeric
        var ids = flux.filter(Conversion::isInteger);
        // names are non-numeric. We delay them to avoid getting 429 code from ncbi server (ddos...)
        var names =
            flux.filter(Predicate.not(Conversion::isInteger)).delayElements(DELAY);
        // concat ids with names converted to ids
        var allIds = ids.concatWith(names.flatMap(this::fromName)).distinct();
        resultPromise = fromIds(allIds)
            .onErrorResume(this::publishError)
            .doOnComplete(() -> {
                if(!genesNotFound.isEmpty())
                    publishWarning(new GenesNotFoundException(genesNotFound));
            }).collectList();
        return this;
    }

    /**
     * Makes a request to the ncbi server to get the id of the gene from its name.
     * The id is returned if found, else the name is added to the <i>genesNoFound</i> {@link List}
     * <br>
     *
     * @param name The name of the gene to find the id of
     * @return A mono encapsulating the name of the gene (present if found)
     */
    public Mono<String> fromName(String name) {
        // TODO: We should url encode the name
        publishState(State.GENE_NAME_TO_ID, name, name);
        logger.log(Level.INFO, "Getting id for gene [{0}]", name);
        return Mono.defer(ThrowingSupplier.sneaky(() -> {
                var json = new URL(String.format(NAME_URL_TEMPLATE, name)).openStream();
                return GeneConverter.extractId(new JsonDocument(json)).switchIfEmpty(notFound(name));
            }))
            .retryWhen(Retry.backoff(NB_RETRY, RETRY_DELAY).filter(IOException.class::isInstance))
            .onErrorResume(Exceptions::isRetryExhausted, e -> Mono.error(e.getCause()))
            .doOnError(e -> logger.log(Level.WARNING,
                String.format("Error while getting id of [%s] gene", name), e))
            .onErrorResume(e -> notFound(name));
    }

    private <T> Mono<T> notFound(String name) {
        return Mono.fromRunnable(() -> genesNotFound.add(name));
    }

    /**
     * Converts the found ids to locus and adds the other to the {@link List list}
     * <i>genesNotFound</i>
     *
     * @param ids A {@link Flux} containing all the ids to convert to locus
     * @return A {@link Flux} containing the {@link Locus locus} for found ids
     */
    public Flux<Locus> fromIds(Flux<String> ids) {
        // We cache the ids because we need them twice (once for creating the url, and once for
        // extracting locus from json) and we don't want to consume the flux twice (it would
        // duplicate all previous actions, like getting ids from names)
        var idsCached = ids.replay().autoConnect();
        return idsCached.collect(Collectors.joining(",")).delayElement(DELAY)
            .flatMap(idString -> {
                publishState(State.GENE_ID_TO_LOCUS, idString, idString);
                return requestIds(idString);
            }).flatMapMany(json -> idsCached.flatMap(
                id -> GeneConverter.extractLocus(id, assemblyAccVer, json).switchIfEmpty(notFound(id))
            ));
    }

    /**
     * Makes the request to ncbi, to get the json document containing ids.<br>
     * Retries the request NB_RETRY times with a RETRY_DELAY delay, augmenting at each retry
     */
    private Mono<JsonDocument> requestIds(String ids) {
        if(ids.isBlank())
            return Mono.error(new NoIdFoundException());
        // TODO: we should url encode the ids
        return Mono.defer(ThrowingSupplier.sneaky(() -> {
                logger.info(String.format("Getting locus for ids : %s", ids));
                var jsonUrl = new URL(String.format(ID_URL_TEMPLATE, ids));
                return Mono.just(new JsonDocument(jsonUrl.openStream()));
            }))
            .retryWhen(Retry.backoff(NB_RETRY, RETRY_DELAY).filter(IOException.class::isInstance))
            .onErrorResume(Exceptions::isRetryExhausted, e -> Mono.error(e.getCause()))
            .doOnError(e -> logger.log(Level.WARNING,
                String.format("Error while requesting locus for ids %s", ids), e));
    }
}
