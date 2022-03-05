package fr.ferret.model.conversions;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
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
    private final int start = 1;
    private final int end = Integer.MAX_VALUE;
    private final Phases1KG phase = Phases1KG.V3;
    private final String vcfPath = "src/test/resources/chr1-africans-phase3.vcf.gz";
    private final IgsrClient igsrClient =
            IgsrClient.builder().chromosome(chr).phase1KG(phase).urlTemplate(vcfPath).build();
    private final ZoneSelection selection = new ZoneSelection().add("AFR", List.of("MSL"));

    @Test
    void testToPed(@TempDir Path tempDir) throws Exception {
        var outPath = tempDir.resolve("test.ped");

        var samples = Resource.getSamples(phase, selection);
        igsrClient.exportVCFFromSamples(new File(vcfPath), start, end, samples);
        VcfConverter.toPed(vcfPath.toString(), outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected = List
                .of("Y016 NA18523 0 0 2 0 T T T T", "SL11 HG03081 0 0 1 0 T T T T",
                        "Y014 NA18520 0 0 2 0 T T T T")
                .stream().map(s -> s.replace(' ', '\t')).toList();
        assertAll(() -> assertEquals(expected, got.subList(0, 3)),
                () -> assertEquals(661, got.size()));
    }

    @Test
    void testToInfo(@TempDir Path tempDir) throws Exception {
        var outPath = tempDir.resolve("test.info");

        var samples = Resource.getSamples(phase, selection);
        igsrClient.exportVCFFromSamples(new File(vcfPath), start, end, samples);
        VcfConverter.toInfo(vcfPath.toString(), outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected = List.of("1:196187886 196187886", "1:196194911 196194911").stream()
                .map(s -> s.replace(' ', '\t')).toList();
        assertAll(() -> assertEquals(expected, got), () -> assertEquals(2, got.size()));
    }

    @Test
    void testToMap(@TempDir Path tempDir) throws Exception {
        var outPath = tempDir.resolve("test.map");

        var samples = Resource.getSamples(phase, selection);
        igsrClient.exportVCFFromSamples(new File(vcfPath), start, end, samples);
        VcfConverter.toMap(vcfPath.toString(), outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected = List.of("1 1:196187886 0 196187886", "1 1:196194911 0 196194911").stream()
                .map(s -> s.replace(' ', '\t')).toList();
        assertAll(() -> assertEquals(expected, got), () -> assertEquals(2, got.size()));
    }

    @Test
    void testToFrq(@TempDir Path tempDir) throws Exception {
        var outPath = tempDir.resolve("test.frq");

        var samples = Resource.getSamples(phase, selection);
        igsrClient.exportVCFFromSamples(new File(vcfPath), start, end, samples);
        VcfConverter.toFrq(vcfPath.toString(), outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected = List.of("1 1:196187886 T . 1.0000 661", "1 1:196194911 T . 1.0000 661")
                .stream().map(s -> s.replace(' ', '\t')).toList();
        assertAll(() -> assertEquals(expected, got), () -> assertEquals(2, got.size()));
    }
}
