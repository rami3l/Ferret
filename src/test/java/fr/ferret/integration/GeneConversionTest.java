package fr.ferret.integration;

import fr.ferret.TestUtils;
import fr.ferret.model.locus.GeneConversion;
import fr.ferret.model.locus.Locus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeneConversionTest {

    @BeforeAll
    void init() throws Exception {
        String idUrlTemplate = "ncbi/gene-id-to-locus/%s.json";
        String nameUrlTemplate = "ncbi/gene-name-to-id/%s.json";
        // TODO: this seems to take effect only after the tests...
        TestUtils.setFinalStatic(GeneConversion.class, "ID_URL_TEMPLATE", idUrlTemplate);
        TestUtils.setFinalStatic(GeneConversion.class, "NAME_URL_TEMPLATE", nameUrlTemplate);
    }


    @Test
    void testCasNominal() {
        // ARRANGE
        var genes = List.of("CR5", "1234");
        var conversion = new GeneConversion(genes, "GCF_000001405.39");

        // ACT
        conversion.start().blockLast();
        var locusList = conversion.getResult();

        // ASSERT
        assertThat(locusList).containsExactly(new Locus("3", 46370141, 46376205),
            new Locus("8", 54073092, 54073992));
    }

    @Test
    void testIdOrNameNotFound_ShouldBeIgnored() {
        // ARRANGE
        var genes = List.of("CR5", "0", "1234", "UNKNOWN");
        var conversion = new GeneConversion(genes, "GCF_000001405.39");

        // ACT
        conversion.start().blockLast();
        var locusList = conversion.getResult();

        // ASSERT
        assertThat(locusList).containsExactly(new Locus("3", 46370141, 46376205),
            new Locus("8", 54073092, 54073992));
    }

}
