package fr.ferret.model;

import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor public class LocusBuilder {

    // Here are the json path to find the information inside the file
    private static final String chromosomePath = "$.result.%s.chromosome";
    private static final String locationHistPath = "$.result.%s.locationhist[*]";

    // Here are the json attribute names to find the information inside the file
    private static final String ASS_ACC_VER = "assemblyaccver";
    private static final String CHR_START = "chrstart";
    private static final String CHR_STOP = "chrstop";

    /**
     * The selected <i>assembly accession version</i>
     */
    private final String assemblyAccVer;

    /**
     * Gets a Locus from a gene id and a gene esummary json file from ncbi server.<br>Here is
     * <a href="https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=gene&id=1234&format=
     * json">an example of ncbi gene esummary file</a>
     *
     * @param id   The id of the gene to find the {@link Locus locus} of
     * @param json An {@link InputStream} pointing to the gene esummary json file
     * @return An {@link Optional optional} {@link Locus locus}
     */
    public Optional<Locus> from(String id, InputStream json) {
        // We parse the json file
        var document = JsonPath.parse(json);

        // We read the chromosome
        String chr = document.read(String.format(chromosomePath, id));

        // We get the list of locations (in the locationhist part)
        List<Map<String, Object>> locations = document.read(String.format(locationHistPath, id));

        // We get the first location of which assemblyaccver is the selected one
        var location =
            locations.stream().filter(l -> assemblyAccVer.equals(l.get(ASS_ACC_VER))).findFirst();

        // We return a Locus created from the chromosome and the start and stop positions
        return location.map(l -> new Locus(chr, (int) l.get(CHR_START), (int) l.get(CHR_STOP)));
    }
}
