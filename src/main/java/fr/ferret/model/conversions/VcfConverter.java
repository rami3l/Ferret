package fr.ferret.model.conversions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import com.pivovarit.function.ThrowingConsumer;
import fr.ferret.utils.Resource;
import htsjdk.tribble.FeatureReader;
import htsjdk.tribble.TribbleIndexedFeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;
import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * The util class to convert `.vcf` files.
 */
@UtilityClass
public class VcfConverter {
    /**
     * List of the possible file extentions, without dot.
     */
    @Getter
    private static final List<String> fileExtensions = List.of("vcf", "frq", "map", "ped", "info");

    /**
     * Generates a new ID for a variant with an empty id {@code "."}. The generated ID will follow
     * the {@code chromosome:position} format.
     */
    public static String generateVariantId(VariantContext ctx) {
        return ctx.hasID() ? ctx.getID() : String.format("%s:%d", ctx.getContig(), ctx.getStart());
    }

    private static FeatureReader<VariantContext> vcfTribbleReader(String vcfPath)
            throws IOException {
        return new TribbleIndexedFeatureReader<>(vcfPath, new VCFCodec(), false);
    }

    private static BufferedWriter truncatingFileWriter(String outPath) throws IOException {
        return Files.newBufferedWriter(Path.of(outPath), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Read the given VCF file and create the corresponding FRQ file, returning its relative path.
     * 
     * @param vcfPath relative path to the VCF file we want to convert.
     */
    public static String toFrq(String vcfPath, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath); var reader = vcfTribbleReader(vcfPath)) {
            var variants = reader.iterator();
            variants.forEach(ThrowingConsumer.unchecked(variant -> {
                var rec = new FrqRecord(variant);
                writer.write(rec.toString());
                writer.newLine();
            }));
            return outPath;
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    /**
     * Read the given VCF file and create the corresponding PED file, returning their relative path.
     * 
     * @param vcfPath path to the VCF file we want to convert.
     * @param outPath path ti the output file.
     */
    public static String toPed(String vcfPath, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath); var reader = vcfTribbleReader(vcfPath)) {
            // A `distilled` VCF file should have for its header all the samples in question.
            var pedigrees = Resource.getPedigrees();
            ((VCFHeader) reader.getHeader()).getGenotypeSamples().stream()
                    .forEach(ThrowingConsumer.unchecked(sample -> {
                        // A pedigree record from the `pedigrees` table.
                        var pedigree = pedigrees.get(sample);
                        var variants = reader.iterator().stream()
                                .map(ctx -> GenotypePair.of(ctx, sample)).toList();
                        var pedRecord = new PedRecord(pedigree, variants);
                        writer.write(pedRecord.toString());
                        writer.newLine();
                    }));
            return outPath;
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    /**
     * Read the given VCF file and create the corresponding MAP file, returning their relative path.
     * 
     * @param vcfPath relative path to the VCF file we want to convert.
     */
    public static String toMap(String vcfPath, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath); var reader = vcfTribbleReader(vcfPath)) {
            for (var variant : reader.iterator()) {
                writer.write(new MapRecord(variant).toString());
                writer.newLine();
            }
            return outPath;
        }
    }

    /**
     * Read the given VCF file and create the corresponding INFO file, returning its relative path.
     * 
     * @param vcfPath relative path to the VCF file we want to convert.
     */
    public static String toInfo(String vcfPath, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath); var reader = vcfTribbleReader(vcfPath)) {
            for (var variant : reader.iterator()) {
                writer.write(new InfoRecord(variant).toString());
                writer.newLine();
            }
            return outPath;
        }
    }
}
