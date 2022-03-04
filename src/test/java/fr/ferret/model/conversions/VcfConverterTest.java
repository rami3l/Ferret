package fr.ferret.model.conversions;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Test
    void testToPed(@TempDir Path tempDir) throws IOException {
        var outPath = tempDir.resolve("test.ped");
        var tempVcfPath = tempDir.resolve("test.vcf");

        var chr = "1";
        var start = 196194909;
        var end = start + 300;
        var selection = new ZoneSelection();
        selection.add("EUR", List.of("GBR"));
        var phase = Phases1KG.V3;
        var samples = Resource.getSamples(phase, selection);
        var igsrClient = IgsrClient.builder().chromosome(chr).phase1KG(Phases1KG.V3).build();

        igsrClient.exportVCFFromSamples(tempVcfPath.toFile(), start, end, samples);
        VcfConverter.toPed(tempVcfPath.toString(), outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected =
                "HG00097 HG00097 0 0 2 0 T T T T A A C C C C T T C C G G A A".replace(' ', '\t');
        assertEquals(expected, got.get(0));
        assertEquals(91, got.size());
    }
}
