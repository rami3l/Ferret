package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class GeneToIdTest {
    @Test
    void testGeneListToID() {

        // Teste si ça fonctionne avec un seul nom
        var geneList = Arrays.asList("KCNT2");
        GeneToId.geneListToID(geneList);
        assertEquals(Arrays.asList("343450"), geneList);

        // Teste si ça fonctionne avec plusieurs noms dont des ids
        geneList = Arrays.asList("KCNT2", "4277", "CCR5");
        GeneToId.geneListToID(geneList);
        assertEquals(Arrays.asList("343450", "4277", "1234"), geneList);

        //Teste s’il y a bien un nullpointerException relevé
        geneList = Arrays.asList("KCTN2");
        GeneToId.geneListToID(geneList);
        assertThrows(NullPointerException, GeneToId.geneListToID(geneList);)
    }
}
