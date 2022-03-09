package fr.ferret.model.locus;

import fr.ferret.model.utils.GeneConverter;
import fr.ferret.model.utils.JsonDocument;
import fr.ferret.utils.Conversion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LocusBuilder {

    private static final Logger logger = Logger.getLogger(LocusBuilder.class.getName());

    // TODO: move these URL templates to a resource file
    private static final String ID_URL_TEMPLATE =
        "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=gene&id=%s&format=json";

    private static final String NAME_URL_TEMPLATE =
        "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term=%s[GENE]%%20AND%%20human[ORGN]&retmode=json";

    /** Delay between requests, to avoid getting response code 429 from ncbi server */
    private static final Duration DELAY = Duration.ofMillis(200);

    /**
     * The selected <i>assembly accession version</i>
     */
    private final String assemblyAccVer;

    private final List<String> genesNotFound = new ArrayList<>();

    /**
     * @param assemblyAccVer The <i>assembly access version</i> to use for getting start and end positions
     */
    public LocusBuilder(String assemblyAccVer) {
        this.assemblyAccVer = assemblyAccVer;
    }

    public Flux<Locus> buildFromVariant(List<String> ids) {
        // TODO: create a flux from ids and delay elements
        // TODO: map with a method which converts a variant id to a Locus and return this flux
        return Flux.empty();
    }

    public Mono<Locus> fromVariant(String id) {
        // TODO: creates a json input stream from url template and id
        // TODO: call VariantConverter with this json and return
        return Mono.empty();
    }

    /**
     * Converts the found gene names/ids to locus.<br>
     * TODO: show a popup containing names/ids of genes not if there are any <br>
     * TODO: show an error popup in case of error (invalid url, network error)
     *
     * @param idsOrNames A {@link List list} of gene names/ids
     * @return A {@link Flux} containing the locus for found genes (empty in case of error).
     */
    public Flux<Locus> buildFromGene(List<String> idsOrNames) {
        var flux = Flux.fromIterable(idsOrNames);
        // ids are numeric
        var ids = flux.filter(Conversion::isInteger);
        // names are non-numeric. We delay them to avoid getting 429 code from ncbi server (ddos...)
        var names =
            flux.filter(Predicate.not(Conversion::isInteger)).delayElements(DELAY);
        // concat ids with names converted to ids
        var allIds = ids.concatWith(names.flatMap(this::fromName)).distinct();
        return fromIds(allIds);
    }

    /**
     * Makes a request to the ncbi server to get the id of the gene from its name.
     * The id is returned if found, else the name is added to the _genesNoFound_ {@link List} <br>
     * TODO: show an error popup in case of error (invalid url, network error)
     *
     * @param name The name of the gene to find the id of
     * @return A mono encapsulating the name of the gene (present if found)
     */
    public Mono<String> fromName(String name) {
        logger.info(String.format("Getting id for gene [%s]", name));
        try {
            var json = new URL(String.format(NAME_URL_TEMPLATE, name)).openStream();
            return GeneConverter.extractId(new JsonDocument(json)).or(notFound(name));
        } catch (Exception e) {
            // TODO: retry n times in case of IOException (could be 429)
            // TODO: error popup (ExceptionHandler)
            logger.log(Level.WARNING, String.format("Error while getting id of [%s] gene", name),
                e);
            return notFound(name);
        }
    }

    private Mono<String> notFound(String name) {
        genesNotFound.add(name);
        return Mono.empty();
    }

    /**
     * Converts the found ids to locus and adds the other to the {@link List list} _genesNotFound_
     *
     * @param ids A {@link Flux} containing all the ids to convert to locus
     * @return A {@link Flux} containing the {@link Locus locus} for found ids
     */
    public Flux<Locus> fromIds(Flux<String> ids) {
        // TODO: we should url encode ids
        // We cache the ids because we need them twice (once for creating the url, and once for
        // extracting locus from json) and we don't want to consume the flux twice (it would
        // duplicate all previous actions, like getting ids from names)
        var idsCached = ids.replay().autoConnect();
        return idsCached.collect(Collectors.joining(",")).delayElement(DELAY)
            .flatMap(idString -> {
                try {
                    logger.info(String.format("Getting locus for ids : %s", idString));
                    var jsonUrl = new URL(String.format(ID_URL_TEMPLATE, idString));
                    return Mono.just(new JsonDocument(jsonUrl.openStream()));
                } catch (Exception e) {
                    // TODO: retry n times in case of IOException (could be 429)
                    // TODO: error popup (ExceptionHandler)
                    logger.log(Level.WARNING,
                        String.format("Error while requesting locus for ids %s", idString), e);
                    return Mono.empty();
                }
            }).flatMapMany(json -> idsCached.flatMap(
                id -> GeneConverter.extractLocus(id, assemblyAccVer, json).or(Mono.defer(() -> {
                    genesNotFound.add(id);
                    return Mono.empty();
                }))
            ));
    }
}
