package fr.ferret.model.conversions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import com.pivovarit.function.ThrowingConsumer;
import fr.ferret.model.vcf.VcfObject;
import fr.ferret.utils.Resource;
import htsjdk.tribble.FeatureReader;
import htsjdk.tribble.TribbleIndexedFeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
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

    private static BufferedWriter truncatingFileWriter(String outPath) throws IOException {
        return Files.newBufferedWriter(Path.of(outPath), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Writes a Frq file from a {@link VcfObject}.
     * 
     * @param vcf The {@link VcfObject} to convert to Frq
     * @param outPath The path to write the Frq file to
     */
    public static String toFrq(VcfObject vcf, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath)) {
            vcf.getVariants().stream().forEach(ThrowingConsumer.unchecked(variant -> {
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
     * Writes a Ped file from a {@link VcfObject}.
     *
     * @param vcf The {@link VcfObject} to convert to Ped
     * @param outPath The path to write the Ped file to
     */
    public static String toPed(VcfObject vcf, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath)) {
            // A `distilled` VCF file should have for its header all the samples in question.
            var pedigrees = Resource.getPedigrees();
            var variantList = vcf.getVariants().toList();
            vcf.getHeader().getGenotypeSamples()
                    .forEach(ThrowingConsumer.unchecked(sample -> {
                        // A pedigree record from the `pedigrees` table.
                        var pedigree = pedigrees.get(sample);
                        var variants = variantList.stream()
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
     * Writes a Map file from a {@link VcfObject}.
     *
     * @param vcf The {@link VcfObject} to convert to Map
     * @param outPath The path to write the Map file to
     */
    public static String toMap(VcfObject vcf, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath);) {
            for (var variant : vcf.getVariants().toList()) {
                writer.write(new MapRecord(variant).toString());
                writer.newLine();
            }
            return outPath;
        }
    }

    /**
     * Writes an Info file from a {@link VcfObject}.
     *
     * @param vcf The {@link VcfObject} to convert to Info
     * @param outPath The path to write the Info file to
     */
    public static String toInfo(VcfObject vcf, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath)) {
            for (var variant : vcf.getVariants().toList()) {
                writer.write(new InfoRecord(variant).toString());
                writer.newLine();
            }
            return outPath;
        }
    }
}
