package fr.ferret.model.utils;

import fr.ferret.model.locus.GeneConversion;
import fr.ferret.utils.Resource;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneConverterTest {

    private InputStream getContent(String file) {
        return Resource.class.getClassLoader().getResourceAsStream(file);
    }

    @Test
    void getLocusFromFile_ShouldReturnLocus() {
        // ARRANGE
        var id = "1234";
        var assemblyAccVer = "GCF_000001405.39";
        var json = new JsonDocument(getContent("gene-id-to-locus.json"));

        // ACT
        var locus = GeneConverter.extractLocus(id, assemblyAccVer, json).blockOptional();
        locus.ifPresentOrElse(System.out::println, () -> System.out.println("Locus not found..."));

        // ASSERT
        assertTrue(locus.isPresent());
        assertEquals("3", locus.get().getChromosome());
        assertEquals(46370141, locus.get().getStart());
        assertEquals(46376205, locus.get().getEnd());
    }

    @Test
    void getIdFromFile_ShouldReturnGeneId() {
        // ARRANGE
        var name = "CR5";
        var json = new JsonDocument(getContent("gene-name-to-id.json"));

        // ACT
        var id = GeneConverter.extractId(json).blockOptional();
        id.ifPresentOrElse(System.out::println, () -> System.out.println("Id not found..."));

        // ASSERT
        assertTrue(id.isPresent());
        assertEquals("22814", id.get());
    }


    /** Tests for the locus builder (not real unit test because it reaches the server) **/

    @Test
    void testLocusBuilder() {
        var genes = List.of("KCNT2", "343450", "CCR5", "1234", "MICB", "4277", "IL6", "3569",
                "APOL1", "8542", "4627", "MYH9");
        // var genes = List.of("CR5", "1234");
        var locusBuilding = new GeneConversion(genes, "GCF_000001405.39");
        locusBuilding.start().doOnComplete(() -> System.out.println(locusBuilding.getResult())).blockLast();
    }

}
