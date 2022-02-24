package fr.ferret.model;

import fr.ferret.utils.Resource;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocusBuilderTest {

    private InputStream getContent(String file) {
        return Resource.class.getClassLoader().getResourceAsStream(file);
    }

    @Test void getLocusFromFile_ShouldReturnLocus() {
        var assemblyAccVer = "GCF_000001405.39";
        var locusBuilder = new LocusBuilder("GCF_000001405.39");
        var json = getContent("gene-id-to-locus.json");
        var locus = locusBuilder.from("1234", json);
        locus.ifPresentOrElse(System.out::println, () -> System.out.println("Locus not found..."));

        assertTrue(locus.isPresent());
        assertEquals("3", locus.get().chromosome());
        assertEquals(46370141, locus.get().start());
        assertEquals(46376205, locus.get().end());
    }
}
