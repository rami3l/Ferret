package fr.ferret.model.utils;

import fr.ferret.model.JsonExtractor;
import fr.ferret.utils.Resource;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneConverterTest {

    private InputStream getContent(String file) {
        return Resource.class.getClassLoader().getResourceAsStream(file);
    }

    @Test void getLocusFromFile_ShouldReturnLocus() {
        // ARRANGE
        var id = "1234";
        var assemblyAccVer = "GCF_000001405.39";
        var json = new JsonExtractor(getContent("gene-id-to-locus.json"));

        // ACT
        var locus = GeneConverter.fromId(id, assemblyAccVer, json);
        locus.ifPresentOrElse(System.out::println, () -> System.out.println("Locus not found..."));

        // ASSERT
        assertTrue(locus.isPresent());
        assertEquals("3", locus.get().getChromosome());
        assertEquals(46370141, locus.get().getStart());
        assertEquals(46376205, locus.get().getEnd());
    }

    @Test void getIdFromFile_ShouldReturnGeneId() {
        // ARRANGE
        var name = "CR5";
        var json = new JsonExtractor(getContent("gene-name-to-id.json"));

        // ACT
        var id = GeneConverter.fromName(name, json);
        id.ifPresentOrElse(System.out::println, () -> System.out.println("Id not found..."));

        // ASSERT
        assertTrue(id.isPresent());
        assertEquals("22814", id.get());
    }
}
