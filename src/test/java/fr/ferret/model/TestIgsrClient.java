package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import fr.ferret.utils.Resource;
import htsjdk.variant.vcf.VCFEncoder;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.jupiter.api.Test;
import fr.ferret.controller.settings.Phases1KG;
import htsjdk.variant.variantcontext.Allele;


class TestIgsrClient {
    @Test
    void testBasicQuery() throws IOException {
        var chr = "1";
        var start = 196194909;
        var end = 196577570;
        var igsrClient = IgsrClient.builder().chromosome(chr).phase1KG(Phases1KG.V3).build();
        try (var reader = igsrClient.reader()) {
            var it = reader.query(chr, start, end);
            assertNotEquals(null, it);
            // `it.next()` is the next line in the iterator.
            var fields = it.next();

            // Fixed fields:
            // #CHROM POS ID REF ALT QUAL FILTER INFO
            // https://samtools.github.io/hts-specs/VCFv4.2.pdf
            // var expected = List.of("1", "196187886", ".", "T", "<CN2>", "100", "PASS");
            assertEquals(196187886, fields.getStart());
            assertEquals(".", fields.getID());
            assertEquals(Allele.REF_T, fields.getReference());
            // TODO: What is "<CN2>"?
            assertEquals(List.of("<CN2>"),
                    fields.getAlternateAlleles().stream().map(Allele::getDisplayString).toList());
            // This position has passed all filters, so nothing fails.
            assertEquals(Set.of(), fields.getFilters());

            // The INFO field contains some key-value pairs...
            var alleleFrequency = fields.getAttributeAsDouble("AF", 0);
            assertEquals(0.000399361, alleleFrequency);
        }
    }

    @Test
    void testSubSampleAndVcfEncode() throws IOException {
        var chr = "1";
        var start = 196194909;
        var end = 196577570;
        var igsrClient = IgsrClient.builder().start(start).end(end).chromosome(chr).phase1KG(Phases1KG.V3).build();
        try(var reader = igsrClient.reader()) {
            var it = reader.query(chr, start, end);
            var fields = it.next();
            var selection = new ZoneSelection();
            selection.add("EUR", List.of("GBR"));
            var samples = Resource.getSamples(Phases1KG.V3, selection);
            var context = fields.subContextFromSamples(samples);
            var encoder = new VCFEncoder((VCFHeader) reader.getHeader(), false, false);
            var vcf = new StringBuilder();
            encoder.write(vcf, context);
            System.out.println(vcf);
            // The result contains elements "{0,1}|{0,1}" for people of the sample and "./." for others but we only want elements for the sample
            // We must also find a way to write the header
        }
    }
}
