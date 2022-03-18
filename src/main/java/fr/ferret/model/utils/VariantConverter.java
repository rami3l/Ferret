package fr.ferret.model.utils;

import fr.ferret.model.hgversion.HgRelease;
import fr.ferret.model.locus.Locus;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@UtilityClass
public class VariantConverter {

    private static final Logger logger = Logger.getLogger(VariantConverter.class.getName());

    // Here are the json attribute names to find the information inside the document
    private static final String ASSEMBLY_PATH = "$.assembly";

    // Here is the json path to find the information inside the document
    private static final String CHROMOSOME = "chr";
    private static final String CHR_START = "chrPosFrom";
    private static final String CHR_STOP = "chrPosTo";

    /**
     * Gets a Locus from a variant json file obtained from the ncbi server.<br>
     * Here is <a href="https://www.ncbi.nlm.nih.gov/projects/SNP/snp_gene.cgi?connect=&rs=1245">
     *     an example of ncbi variant file</a><br>The json should have this structure:
     * <pre>{@code
     * {
     *     ...
     *     "assembly" : {
     *         "CRCh37.p13" : [{
     *             "chrPosFrom" : "67757160",
     *             "chr" : "16",
     *             "chrPosTo" : "67757160",
     *             ...
     *         }],
     *         "GRCh38.p7" : [{
     *             ...
     *         }],
     *         ...
     *     }
     * }
     * }</pre>
     * Amongst the json parts which correspond to the given hgVersion (GRCh37 for example), the one
     * with the higher patch (p13 for example) is kept.<br>This block is a list containing one
     * dictionary from which is extracted the values of <i>chrPosFrom</i>, <i>chr</i> and
     * <i>chrPosTo</i> in order to construct the {@link Locus}.<br><br>
     * NB: the list contain only one element, so we take the first element of the list.
     *
     * @param hgVersion The hgVersion to use for converting the variant to a {@link Locus}
     * @param document The variant {@link JsonDocument}
     * @return A {@link Mono} encapsulating the extracted {@link Locus}, present if found
     */
    public Mono<Locus> extractLocus(String hgVersion, JsonDocument document) {
        return Mono.defer(() -> {
            Map<String, List<Map<String, String>>> assembly = document.get(ASSEMBLY_PATH);
            return Flux.fromIterable(assembly.entrySet())
                // From each entry of the assembly object, we create an entry using the hgRelease
                // as key and the first element of the list (which is a map containing the locus
                // information) as value
                .map(entry -> Map.entry(HgRelease.from(entry.getKey()), entry.getValue().get(0)))
                // We only keep the entries with the requested hgVersion
                .filter(entry -> hgVersion.equals(entry.getKey().getHgVersion()))
                // We only keep the entry with the latest patch for the given hgVersion
                .sort(Comparator.comparing(entry -> entry.getKey().getPatch())).last()
                // We create a locus from the entry
                .map(Map.Entry::getValue)
                .map(locusData -> new Locus(locusData.get(CHROMOSOME),
                    Integer.parseInt(locusData.get(CHR_START)),
                    Integer.parseInt(locusData.get(CHR_STOP))));
        }).doOnError(e -> logger.log(Level.INFO, "Cannot extract variant locus", e))
            .onErrorResume(e -> Mono.empty()).onErrorStop();
    }

}
