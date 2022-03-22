package fr.ferret.model.conversions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VcfConverterTest {

    private final String vcfPath = "src/test/resources/1kg/phase3/46370700-46371000-EUR-GBR.vcf";

    @Test
    void testToPed(@TempDir Path tempDir) throws Exception {
        var outPath = tempDir.resolve("test.ped");

        VcfConverter.toPed(vcfPath, outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected =
            Stream.of("HG00097 HG00097 0 0 2 0 G G C C A G T T G G G G A A A A T T T T G G",
                    "HG00130 HG00130 0 0 2 0 G G C C G A T T G G G G A A A A T T T T G G",
                    "HG00251 HG00251 0 0 1 0 G G C C G G T T G G G G A A A A T T T T G C",
                    "HG00096 HG00096 0 0 1 0 G G C C A A T T G G G G A A A A T T T T G G",
                    "HG00250 HG00250 0 0 2 0 G G C C A A T T G G G G A A A A T T T T G G")
                .map(s -> s.replace(' ', '\t')).toList();
        assertAll(() -> assertEquals(expected, got.subList(0, 5)),
                () -> assertEquals(91, got.size()));
    }

    @Test
    void testToInfo(@TempDir Path tempDir) throws Exception {
        var outPath = tempDir.resolve("test.info");

        VcfConverter.toInfo(vcfPath, outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected = Stream.of("3:46370719 46370719", "3:46370803 46370803",
                "3:46370804 46370804", "3:46370830 46370830", "3:46370885 46370885",
                "3:46370888 46370888", "3:46370901 46370901", "3:46370919 46370919",
                "3:46370923 46370923", "3:46370974 46370974", "3:46370978 46370978")
                .map(s -> s.replace(' ', '\t')).toList();
        assertAll(() -> assertEquals(expected, got), () -> assertEquals(11, got.size()));
    }

    @Test
    void testToMap(@TempDir Path tempDir) throws Exception {
        var outPath = tempDir.resolve("test.map");

        VcfConverter.toMap(vcfPath, outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected = Stream.of("3 3:46370719 0 46370719", "3 3:46370803 0 46370803",
                "3 3:46370804 0 46370804", "3 3:46370830 0 46370830", "3 3:46370885 0 46370885",
                "3 3:46370888 0 46370888", "3 3:46370901 0 46370901", "3 3:46370919 0 46370919",
                "3 3:46370923 0 46370923", "3 3:46370974 0 46370974", "3 3:46370978 0 46370978")
            .map(s -> s.replace(' ', '\t')).toList();
        assertAll(() -> assertEquals(expected, got), () -> assertEquals(11, got.size()));
    }

    @Test
    void testToFrq(@TempDir Path tempDir) throws Exception {
        var outPath = tempDir.resolve("test.frq");

        VcfConverter.toFrq(vcfPath, outPath.toString());

        var got = Files.readAllLines(outPath);
        var expected = Stream.of("3 3:46370719 G . 1.0000 91", "3 3:46370803 C T 0.9286 91",
                "3 3:46370804 G A 0.5000 91", "3 3:46370830 T . 1.0000 91", "3 3:46370885 G . 1.0000 91",
                "3 3:46370888 G . 1.0000 91", "3 3:46370901 A . 1.0000 91", "3 3:46370919 A . 1.0000 91",
                "3 3:46370923 T . 1.0000 91", "3 3:46370974 T . 1.0000 91", "3 3:46370978 G C 0.9945 91")
            .map(s -> s.replace(' ', '\t')).toList();
        assertAll(() -> assertEquals(expected, got), () -> assertEquals(11, got.size()));
    }
}
