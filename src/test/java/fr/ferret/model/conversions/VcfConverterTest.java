package fr.ferret.model.conversions;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.model.IgsrClient;
import fr.ferret.model.ZoneSelection;
import fr.ferret.utils.Resource;

class VcfConverterTest {
    private final String chr = "1";
    // private final int start = 196194909;
    private final int start = 1;
    private final int end = Integer.MAX_VALUE;
    private final Phases1KG phase = Phases1KG.V3;
    private final String vcfPath = "src/test/resources/chr1-africans-phase3.vcf.gz";
    private final IgsrClient igsrClient =
            IgsrClient.builder().chromosome(chr).phase1KG(phase).urlTemplate(vcfPath).build();

    @Test
    void testToPed(@TempDir Path tempDir) throws IOException {
        var outPath = tempDir.resolve("test.ped");

        var selection = new ZoneSelection();
        selection.add("AFR", List.of("MSL"));
        var phase = Phases1KG.V3;
        var samples = Resource.getSamples(phase, selection);

        igsrClient.exportVCFFromSamples(new File(vcfPath), start, end, samples);
        VcfConverter.toPed(vcfPath.toString(), outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected = "Y016 NA18523 0 0 2 0 T T T T".replace(' ', '\t');
        assertAll(() -> assertEquals(expected, got.get(0)), () -> assertEquals(661, got.size()));
    }
}
