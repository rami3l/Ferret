package fr.ferret.integration;

import fr.ferret.TestUtils;
import fr.ferret.model.locus.Locus;
import fr.ferret.model.locus.VariantConversion;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VariantConversionTest {

    @BeforeAll
    void init() throws Exception {
        String idUrlTemplate = "ncbi/variant-id-to-locus/%s.json";
        // TODO: this seems to take effect only after the tests...
        TestUtils.setFinalStatic(VariantConversion.class, "ID_URL_TEMPLATE", idUrlTemplate);
    }

    @Test
    void testNominalCase() {
        // ARRANGE
        var hgVersion = "GRCh38";
        var variants = List.of("1257");
        var conversion = new VariantConversion(variants, hgVersion);

        // ACT
        conversion.start().blockLast();
        var locusList = conversion.getResult();
        System.out.println(locusList);

        // ASSERT
        assertThat(locusList).containsExactly(new Locus("8", 6615836, 6615836));
    }
}
