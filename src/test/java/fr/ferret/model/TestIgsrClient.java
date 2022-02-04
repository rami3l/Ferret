package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;


public class TestIgsrClient {
    @Test
    public void testBasicQuery() throws IOException {
        var chr = 1;
        var start = 196194909;
        var end = 196577570;
        var igsrClient = IgsrClient.builder().chromosome(chr).build();
        try (var reader = igsrClient.reader();) {
            var it = reader.query(Integer.toString(chr), start, end);
            assertNotEquals(null, it);
            // #CHROM POS ID REF ALT QUAL FILTER INFO
            // Split whitespaces in the spec line.
            var fields = Arrays.asList(it.next().split("\\s+"));
            var expected = List.of("1", "196187886", ".", "T", "<CN2>", "100", "PASS");
            assertEquals(expected, fields.subList(0, expected.size()));
        }
    }
}
