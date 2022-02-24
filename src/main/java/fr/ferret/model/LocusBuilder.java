package fr.ferret.model;

import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class LocusBuilder {

    private static final String chromosomePath = "$.result.%s.chromosome";
    private static final String locationHistPath = "$.result.%s.locationhist[*]";

    private static final String ASS_ACC_VER = "assemblyaccver";
    private static final String CHR_START = "chrstart";
    private static final String CHR_STOP = "chrstop";

    private final String assemblyAccVer;

    public Optional<Locus> from(String id, InputStream json) {
        var document = JsonPath.parse(json);
        String chr = document.read(String.format(chromosomePath, id));
        List<Map<String, Object>> locations = document.read(String.format(locationHistPath, id));
        var location =
            locations.stream().filter(l -> assemblyAccVer.equals(l.get(ASS_ACC_VER))).findFirst();
        return location.map(l -> new Locus(chr, (int) l.get(CHR_START), (int) l.get(CHR_STOP)));
    }
}
