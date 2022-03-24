package fr.ferret.model;

import fr.ferret.model.utils.FileWriter;
import fr.ferret.model.utils.VcfUtils;
import fr.ferret.model.vcf.IgsrClient;
import fr.ferret.model.vcf.VcfObject;
import fr.ferret.utils.Resource;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class IgsrClientTest {
    private final String chr = "3";
    private final int start = 46370804;
    private final int end = 46370880;
    private final Phase1KG phase = new Phase1KG("phase3", "Phase 3");
    private final String vcfPath = "src/test/resources/1kg/phase3/CCR5-Europeans.vcf.gz";

    @Test
    void testBasicQuery() throws IOException {

        var igsrClient = new IgsrClient(vcfPath);
        var reader = igsrClient.getReader(chr).block();
        assertNotNull(reader);

        var it = reader.query(chr, start, end);
        var fields = it.next();
        it.close();
        reader.close();

        assertAll(
                // Fixed fields:
                // #CHROM POS ID REF ALT QUAL FILTER INFO
                // https://samtools.github.io/hts-specs/VCFv4.2.pdf
                () -> assertEquals(46370804, fields.getStart()),
                () -> assertEquals(".", fields.getID()),
                // REF: Allele 2 code (missing = 'N')
                () -> assertEquals(Allele.REF_G, fields.getReference()),
                // ALT: Allele 1 code (missing = '.')
                // `<CN2>` means "Copy Number = 2"
                // See: https://www.biostars.org/p/232205/
                () -> assertEquals(List.of("A"),
                        fields.getAlternateAlleles().stream().map(Allele::getDisplayString)
                                .toList()),
                // This position has passed all filters, so nothing fails.
                () -> assertEquals(Set.of(), fields.getFilters()),

                // The INFO field contains some key-value pairs...
                // eg. "AF" for Allele Frequency...
                () -> assertEquals(0.338458, fields.getAttributeAsDouble("AF", 0)),

                // This is how you get the info of an individual...
                () -> assertEquals("A|A", fields.getGenotype("NA20832").getGenotypeString()),
                () -> assertEquals("G|A", fields.getGenotype("NA20826").getGenotypeString()),
                () -> assertEquals("G|G", fields.getGenotype("NA20822").getGenotypeString()),
                () -> assertEquals("A|G", fields.getGenotype("NA20813").getGenotypeString())
        );

        igsrClient.close();
    }

    @Test
    void testWriteVCFFromSample(@TempDir Path tempDir) throws IOException {

        try (var igsrClient = new IgsrClient(vcfPath)) {
            var reader = igsrClient.getReader(chr).block();
            var it = reader.query(chr, start, end);

            var samples = Resource.getSample(phase).stream()
                .filter(region -> "EUR".equals(region.getAbbrev()))
                .flatMap(region -> region.getZones().stream())
                .filter(zone -> "GBR".equals(zone.getAbbrev()))
                .map(Zone::getPeople)
                .findFirst().orElseThrow();

            var variants =
                    it.stream().map(variant -> variant.subContextFromSamples(samples)).toList();
            var oldHeader = (VCFHeader) reader.getHeader();

            var header = VcfUtils.subVCFHeaderFromSamples(oldHeader, samples);

            var tempVcf = tempDir.resolve("test.vcf");
            FileWriter.writeVCF(new VcfObject(header, variants.iterator()), tempVcf.toString());

            try (var tempReader = new VCFFileReader(tempVcf.toFile(), false)) {
                assertAll(() -> assertTrue(Files.exists(tempVcf)),
                        // The new header is truncated, the size of which equals to that of the
                        // samples.
                        () -> assertNotEquals(samples.size(), oldHeader.getNGenotypeSamples()),
                        () -> assertEquals(samples.size(), header.getNGenotypeSamples()),
                        // The size of contexts remains the same before and after truncation.
                        () -> assertEquals(variants.size(), tempReader.iterator().stream().count())
                );
            }
        }
    }
}
