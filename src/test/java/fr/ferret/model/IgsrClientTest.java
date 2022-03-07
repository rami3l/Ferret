package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import fr.ferret.model.vcf.IgsrClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.model.utils.FileWriter;
import fr.ferret.model.utils.VCFHeaderExt;
import fr.ferret.utils.Resource;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;


class IgsrClientTest {
    private final String chr = "1";
    private final int start = 114514;
    private final int end = 196194913;
    private final Phases1KG phase = Phases1KG.V3;
    private final String vcfPath = "src/test/resources/chr1-africans-phase3.vcf.gz";
    private final IgsrClient igsrClient =
            IgsrClient.builder().phase1KG(phase).urlTemplate(vcfPath).build();

    @Test
    void testBasicQuery() throws IOException {

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
                () -> assertEquals(196187886, fields.getStart()),
                () -> assertEquals(".", fields.getID()),
                // REF: Allele 2 code (missing = 'N')
                () -> assertEquals(Allele.REF_T, fields.getReference()),
                // ALT: Allele 1 code (missing = '.')
                // `<CN2>` means "Copy Number = 2"
                // See: https://www.biostars.org/p/232205/
                () -> assertEquals(List.of(),
                        fields.getAlternateAlleles().stream().map(Allele::getDisplayString)
                                .toList()),
                // This position has passed all filters, so nothing fails.
                () -> assertEquals(Set.of(), fields.getFilters()),

                // The INFO field contains some key-value pairs...
                // eg. "AF" for Allele Frequency...
                () -> assertEquals(0.000399361, fields.getAttributeAsDouble("AF", 0)),

                // This is how you get the info of an individual...
                () -> assertEquals("T|T", fields.getGenotype("NA18523").getGenotypeString()));
    }

    @Test
    void testWriteVCFFromSample(@TempDir Path tempDir) throws IOException {

        try (var reader = igsrClient.getReader(chr).block(); var it = reader.query(chr, start, end)) {
            var selection = new ZoneSelection();
            selection.add("AFR", List.of("MSL"));
            var samples = Resource.getSamples(phase, selection);
            var variants =
                    it.stream().map(variant -> variant.subContextFromSamples(samples)).toList();
            var oldHeader = (VCFHeader) reader.getHeader();

            var header = VCFHeaderExt.subVCFHeaderFromSamples(oldHeader, samples);

            var tempVcfPath = tempDir.resolve("test.vcf");
            var tempVcf = tempVcfPath.toFile();
            FileWriter.writeVCF(tempVcf, header, variants.stream());

            try (var tempReader = new VCFFileReader(tempVcfPath.toFile(), false)) {
                assertAll(() -> assertTrue(Files.exists(tempVcfPath)),
                        // The new header is truncated, the size of which equals to that of the
                        // samples.
                        () -> assertNotEquals(samples.size(), oldHeader.getNGenotypeSamples()),
                        () -> assertEquals(samples.size(), header.getNGenotypeSamples()),
                        // The size of contexts remains the same before and after truncation.
                        () -> assertEquals(variants.size(), tempReader.iterator().stream().count())
                        // The wrapped method works in exactly the same way as this test case.
                        //() -> {
                        //    var oldContent = Files.readString(tempVcfPath);
                        //    var newTempVCFPath = tempDir.resolve("test2.vcf");
                        //    var newTempVCF = newTempVCFPath.toFile();
                        //    igsrClient.exportVCFFromSamples(newTempVCF, start, end, selection)
                        //            .blockLast();
                        //    assertEquals(oldContent, Files.readString(newTempVCFPath));
                        );
            }
        }
    }
}
