package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import fr.ferret.controller.settings.Phases1KG;


public class TestIgsrClient {
    @Test
    public void testBasicQuery() throws IOException {
        var chr = "1";
        var start = 196194909;
        var end = 196577570;
        var igsrClient = IgsrClient.builder().chromosome(chr).phase1KG(Phases1KG.V3).build();
        try (var reader = igsrClient.reader();) {
            var it = reader.query(chr, start, end);
            assertNotEquals(null, it);
            // `it.next()` is the next line in the iterator.
            // Split whitespaces in the spec line to get all fields.
            var fields = Arrays.asList(it.next().split("\\s+"));
            // Fixed fields:
            // #CHROM POS ID REF ALT QUAL FILTER INFO
            // https://samtools.github.io/hts-specs/VCFv4.2.pdf
            var expected = List.of("1", "196187886", ".", "T", "<CN2>", "100", "PASS");
            assertEquals(expected, fields.subList(0, expected.size()));
            // Optional fields: see the link above.
        }
    }

    @Test
    public void testGetAllPopulations() {
        var chr = "1";
        var start = 196194909;
        var end = 196194913;
        var igsrClient = IgsrClient.builder().chromosome(chr).start(start).end(end)
                .phase1KG(Phases1KG.V3).build();
        var elements = igsrClient.getAllPopulations();
        var expectedLine1 = List.of("1", "196187886", ".", "T", "<CN2>", "100", "PASS");
        var expectedLine2 = List.of("1", "196194911", ".", "T", "C", "100", "PASS");
        assertEquals(expectedLine1, elements.get(0).subList(0, expectedLine1.size()));
        assertEquals(expectedLine2, elements.get(1).subList(0, expectedLine2.size()));
    }
}
