package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.io.IOException;
import org.junit.jupiter.api.Test;


public class TestIgsrClient {
    @Test
    public void testBasicQuery() {
        var chr = 1;
        var start = 196194909;
        var end = 196577570;
        var igsrClient = IgsrClient.builder().chromosome(chr).build();
        try (var reader = igsrClient.reader();) {
            var it = reader.query(Integer.toString(chr), start, end);
            assertNotEquals(null, it);
            assertEquals("what?", it.next());
        } catch (IOException e) {
            // TODO: Handle this exception
        }
    }
}
