package fr.ferret.model.utils;

import fr.ferret.model.locus.VariantConversion;
import fr.ferret.utils.Resource;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VariantConverterTest {

    private InputStream getContent(String file) {
        return Resource.class.getClassLoader().getResourceAsStream(file);
    }

    @Test
    void getLocusFromJson_shouldReturnLocus() {
        // ARRANGE
        var hgVersion = "GRCh38";
        var json = new JsonDocument(getContent("variant-id-to-locus.json"));

        // ACT
        var locus = VariantConverter.extractLocus(hgVersion, json).blockOptional();
        locus.ifPresentOrElse(System.out::println, () -> System.out.println("Locus not found..."));

        // ASSERT
        assertTrue(locus.isPresent());
        assertEquals("8", locus.get().getChromosome());
        assertEquals(6615836, locus.get().getStart());
        assertEquals(6615836, locus.get().getEnd());
    }

    /** Test for the variant conversion (not real unit test because it reaches the server) **/

    @Test
    void testVariantConversion() {
        var variants = List.of("1245", "1246");
        var variantConversion = new VariantConversion(variants, "GRCh38");
        variantConversion.start().doOnComplete(() -> System.out.println(variantConversion.getResult())).blockLast();
    }
}
