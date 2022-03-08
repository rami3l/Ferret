package fr.ferret.controller.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FerretConfigTest {
    @Test
    void testSave(@TempDir Path tempDir) throws Exception {
        var cfg = FerretConfig.builder().build();
        var tempFile = tempDir.resolve(FerretConfig.DEFAULT_FILENAME);
        cfg.save(tempFile);
        var got = Files.readString(tempFile);
        var expected = """
                maf-threshold=0.0
                selected-human-genome=HG19
                selected-output-type=ALL
                selected-version=V3
                """;
        assertEquals(expected, got);
    }

    @Test
    void testLoad(@TempDir Path tempDir) throws Exception {
        var cfgStr = """
                maf-threshold=0.0
                selected-human-genome=HG19
                selected-output-type=ALL
                selected-version=V3
                """;
        var tempFile = tempDir.resolve(FerretConfig.DEFAULT_FILENAME);
        Files.write(tempFile, cfgStr.getBytes());

        var expected = FerretConfig.builder().mafThreshold(0)
                .selectedHumanGenome(HumanGenomeVersions.HG19)
                .selectedOutputType(FileOutputType.ALL).selectedVersion(Phases1KG.V3).build();
        var got = FerretConfig.load(tempFile);
        assertEquals(expected, got);
    }
}

