package fr.ferret.model.locus;

import fr.ferret.model.utils.GeneConverter;
import fr.ferret.model.utils.JsonDocument;
import fr.ferret.utils.Conversion;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocusBuilder {

    // TODO: move these URL templates to a resource file
    private static final String ID_URL_TEMPLATE =
        "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=gene&id=%s&format=json";

    private static final String NAME_URL_TEMPLATE =
        "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term=%s[GENE]%20AND%20human[ORGN]&retmode=json";

    /**
     * The selected <i>assembly accession version</i>
     */
    private final String assemblyAccVer;

    private JsonDocument locusExtractor;

    private final List<String> genesNotFound = new ArrayList<>();

    /**
     * @param assemblyAccVer The <i>assembly access version</i> to use for getting start and end positions
     */
    public LocusBuilder(String assemblyAccVer) {
        this.assemblyAccVer = assemblyAccVer;
    }

    /**
     * Converts the found gene names/ids to locus.
     * TODO: show a popup containing names/ids of genes not if there are any
     * TODO: show an error popup in case of error (invalid url, network error)
     *
     * @param idsOrNames A {@link List list} of gene names/ids
     * @return A {@link Stream stream} containing the locus for found genes. This stream is empty
     * in case of error.
     */
    public Stream<Locus> buildFrom(List<String> idsOrNames) {
        var ids = idsOrNames.stream().filter(Conversion::isInteger);
        var names = idsOrNames.stream().filter(Predicate.not(Conversion::isInteger));
        var allIds = Stream.concat(ids, fromNames(names)).collect(Collectors.toSet());
        try {
            var jsonUrl = new URL(String.format(ID_URL_TEMPLATE, String.join(",", allIds)));
            locusExtractor = new JsonDocument(jsonUrl.openStream());
        } catch (Exception e) {
            // TODO: error popup (ExceptionHandler)
            return Stream.empty();
        }
        var locusStream = fromIds(allIds.stream());
        if(!genesNotFound.isEmpty()) {
            // TODO: alert popup with genes not converted to locus
        }
        return locusStream;
    }

    /**
     * Converts the found names to ids and adds the other to the {@link List list} _genesNoFound_
     * TODO: show an error popup in case of error (invalid url, network error)
     *
     * @param names A {@link Stream stream} containing all the names to convert to ids
     * @return A {@link Stream stream} containing the ids for found names
     */
    private Stream<String> fromNames(Stream<String> names) {
        // TODO: we should url encode names
        Stream<Optional<String>> stream = names.map(name -> {
            try {
                var json = new URL(String.format(NAME_URL_TEMPLATE, name)).openStream();
                return GeneConverter.extractId(new JsonDocument(json)).or(() -> {
                    genesNotFound.add(name);
                    return Optional.empty();
                });
            } catch (Exception e) {
                // TODO: error popup (ExceptionHandler)
                genesNotFound.add(name);
                return Optional.empty();
            }
        });
        return stream.flatMap(Optional::stream);
    }

    /**
     * Converts the found ids to locus and adds the other to the {@link List list} _genesNotFound_
     *
     * @param ids A {@link Stream stream} containing all the ids to convert to locus
     * @return A {@link Stream stream} containing the {@link Locus locus} for found ids
     */
    private Stream<Locus> fromIds(Stream<String> ids) {
        // TODO: we should url encode ids
        return ids.map(id -> GeneConverter.extractLocus(id, assemblyAccVer, locusExtractor).or(() -> {
            genesNotFound.add(id);
            return Optional.empty();
        })).flatMap(Optional::stream);
    }
}
