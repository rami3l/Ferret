package fr.ferret.model.conversions;

import fr.ferret.model.Phase1KG;
import fr.ferret.model.ZoneSelection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VcfConverterTest {
    private final String chr = "1";
    //private final int start = 196194909;
    //private final int end = 196194913;
    private final Phase1KG phase = new Phase1KG("phase3", "Phase 3");
    //private final String vcfSource = "src/test/resources/chr1-africans-phase3.vcf.gz";
    private final String vcfPath = "src/test/resources/chr1-africans-phase3.vcf";
    // TODO: if we need toRegion::getZones correct this → see IgsrClientTest L72
    private final ZoneSelection selection = new ZoneSelection(); //.add("AFR", List.of("MSL"));


    //@BeforeAll
    //void init() {
    //    new VcfExport(Flux.just(new Locus(chr, start, end))).setFilter(selection)
    //        .start(new File(vcfPath)).blockLast();
    //}

    //@Test
    //void testToPed(@TempDir Path tempDir) throws Exception {
    //    var outPath = tempDir.resolve("test.ped");
    //
    //    var samples = Resource.getSamples(phase, selection);
    //    VcfConverter.toPed(vcfPath, outPath.toString());
    //
    //    var got = Files.readAllLines(outPath);
    //    var expected = Stream.of("Y016 NA18523 0 0 2 0 T T T T", "SL11 HG03081 0 0 1 0 T T T T",
    //            "Y014 NA18520 0 0 2 0 T T T T").map(s -> s.replace(' ', '\t')).toList();
    //    assertAll(() -> assertEquals(expected, got.subList(0, 3)),
    //            () -> assertEquals(661, got.size()));
    //}

    @Test
    void testToInfo(@TempDir Path tempDir) throws Exception {
        var outPath = tempDir.resolve("test.info");

        var samples = selection.getSample();
        VcfConverter.toInfo(vcfPath, outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected = Stream.of("1:196187886 196187886", "1:196194911 196194911")
                .map(s -> s.replace(' ', '\t')).toList();
        assertAll(() -> assertEquals(expected, got), () -> assertEquals(2, got.size()));
    }

    @Test
    void testToMap(@TempDir Path tempDir) throws Exception {
        var outPath = tempDir.resolve("test.map");

        var samples = selection.getSample();
        VcfConverter.toMap(vcfPath, outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected = Stream.of("1 1:196187886 0 196187886", "1 1:196194911 0 196194911")
                .map(s -> s.replace(' ', '\t')).toList();
        assertAll(() -> assertEquals(expected, got), () -> assertEquals(2, got.size()));
    }

    //@Test
    //void testToFrq(@TempDir Path tempDir) throws Exception {
    //    var outPath = tempDir.resolve("test.frq");
    //
    //    var samples = Resource.getSamples(phase, selection);
    //    VcfConverter.toFrq(vcfPath, outPath.toString());
    //
    //    var got = Files.readAllLines(outPath);
    //    var expected = Stream.of("1 1:196187886 T . 1.0000 661", "1 1:196194911 T . 1.0000 661")
    //        .map(s -> s.replace(' ', '\t')).toList();
    //    assertAll(() -> assertEquals(expected, got), () -> assertEquals(2, got.size()));
    //}
}
