package fr.ferret.model.utils;

import fr.ferret.model.locus.Locus;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@UtilityClass public class GeneConverter {
    // Here are the json paths to find the information inside the files
    private static final String CHROMOSOME_PATH = "$.result.%s.chromosome";
    private static final String LOCATION_HIST_PATH = "$.result.%s.locationhist[*]";
    private static final String ID_PATH = "$.esearchresult.idlist[0]";

    // Here are the json attribute names to find the information inside the file
    private static final String ASS_ACC_VER = "assemblyaccver";
    private static final String CHR_START = "chrstart";
    private static final String CHR_STOP = "chrstop";

    /**
     * Gets a Locus from a gene id and a gene esummary json file, obtained from ncbi server.<br>
     * Here is <a href="https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=gene&id=1234
     * &format=json">an example of ncbi gene esummary file</a>
     *
     * @param id             The id of the gene to find the {@link Locus locus} of
     * @param assemblyAccVer The <i>assembly access version</i> to use for getting start and end positions
     * @param document       The JsonExtractor to get the {@link Locus locus} from
     * @return An {@link Optional optional} {@link Locus locus}
     */
    public Optional<Locus> extractLocus(String id, String assemblyAccVer, JsonDocument document) {
        try {
            // We read the chromosome
            String chr = document.get(String.format(CHROMOSOME_PATH, id));

            // We get the list of locations (in the locationhist part)
            List<Map<String, Object>> locations =
                document.get(String.format(LOCATION_HIST_PATH, id));

            // We get the first location of which assemblyaccver is the selected one
            var location = locations.stream().filter(l -> assemblyAccVer.equals(l.get(ASS_ACC_VER)))
                .findFirst();

            // We return a Locus created from the chromosome and the start and stop positions
            return location.map(l -> new Locus(chr, (int) l.get(CHR_START), (int) l.get(CHR_STOP)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a gene id from a gene esummary json file obtained by the gene name from the ncbi server.
     * <br>Here is <a href="https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term=
     * CR5[GENE]%20AND%20human[ORGN]&retmode=json">an example of esummary file obtained by gene name</a>
     *
     * @param document The {@link JsonDocument} encapsulating the json to extract the gene id from
     * @return an {@link Optional optional} id (present if found in the json)
     */
    public static Optional<String> extractId(JsonDocument document) {
        try {
            return Optional.ofNullable(document.get(ID_PATH));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
