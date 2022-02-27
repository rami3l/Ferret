package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.model.utils.FileWriter;
import fr.ferret.model.utils.VCFHeaderExt;
import fr.ferret.utils.Resource;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;


class TestIgsrClient {
    @Test
    void testBasicQuery() throws IOException {
        var chr = "1";
        var start = 196194909;
        var end = 196577570;
        var igsrClient = IgsrClient.builder().chromosome(chr).phase1KG(Phases1KG.V3).build();
        try (var reader = igsrClient.reader(); var it = reader.query(chr, start, end)) {
            assertNotEquals(null, it);
            // `it.next()` is the next line in the iterator.
            var fields = it.next();

            assertAll(
                    // Fixed fields:
                    // #CHROM POS ID REF ALT QUAL FILTER INFO
                    // https://samtools.github.io/hts-specs/VCFv4.2.pdf
                    // var expected = List.of("1", "196187886", ".", "T", "<CN2>", "100", "PASS");
                    () -> assertEquals(196187886, fields.getStart()),
                    () -> assertEquals(".", fields.getID()),
                    () -> assertEquals(Allele.REF_T, fields.getReference()),
                    // TODO: What is "<CN2>"?
                    () -> assertEquals(List.of("<CN2>"),
                            fields.getAlternateAlleles().stream().map(Allele::getDisplayString)
                                    .toList()),
                    // This position has passed all filters, so nothing fails.
                    () -> assertEquals(Set.of(), fields.getFilters()),

                    // The INFO field contains some key-value pairs...
                    // eg. "AF" for Allele Frequency...
                    () -> assertEquals(0.000399361, fields.getAttributeAsDouble("AF", 0)));
        }
    }

    @Test
    void testWriteVCFFromSample(@TempDir Path tempDir) throws IOException {
        var chr = "1";
        var start = 196194909;
        var end = 196194913;
        var igsrClient = IgsrClient.builder().chromosome(chr).phase1KG(Phases1KG.V3).build();
        try (var reader = igsrClient.reader(); var it = reader.query(chr, start, end)) {
            var selection = new ZoneSelection();
            selection.add("EUR", List.of("GBR"));
            var phase = Phases1KG.V3;
            var samples = Resource.getSamples(phase, selection);
            var contexts =
                    it.stream().map(context -> context.subContextFromSamples(samples)).toList();
            var oldHeader = (VCFHeader) reader.getHeader();

            var header = VCFHeaderExt.subVCFHeaderFromSamples(oldHeader, samples);

            var tempVcfPath = tempDir.resolve("test.vcf");
            var tempVcf = tempVcfPath.toFile();
            FileWriter.writeVCF(tempVcf, header, contexts.stream());

            try (var tempReader = new VCFFileReader(tempVcfPath.toFile())) {
                assertAll(() -> assertTrue(Files.exists(tempVcfPath)),
                        // The new header is truncated, the size of which equals to that of the
                        // samples.
                        () -> assertNotEquals(samples.size(), oldHeader.getNGenotypeSamples()),
                        () -> assertEquals(samples.size(), header.getNGenotypeSamples()),
                        // The size of contexts remains the same before and after truncation.
                        () -> assertEquals(contexts.size(), tempReader.iterator().stream().count()),
                        // The wrapped method works in exactly the same way as this test case.
                        () -> {
                            var oldContent = Files.readString(tempVcfPath);
                            var newTempVCFPath = tempDir.resolve("test2.vcf");
                            var newTempVCF = newTempVCFPath.toFile();
                            igsrClient.exportVCFFromSamples(newTempVCF, start, end, phase,
                                    selection);
                            assertEquals(oldContent, Files.readString(newTempVCFPath));
                        });
            }
        }
    }
}
