package fr.ferret.model.conversions;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
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
     * Read the given VCF file and create the corresponding FRQ file, returning its relative path.
     * 
     * @param vcfFilePath relative path to the VCF file we want to convert.
     */
    public static String toFrq(String vcfFilePath) {
        // TODO: complete this method
        return vcfFilePath;
    }

    /**
     * Read the given VCF file and create the corresponding PED file, returning their relative path.
     * 
     * @param vcfPath path to the VCF file we want to convert.
     * @param outPath path ti the output file.
     */
    public static String toPed(String vcfPath, String outPath) throws IOException {
        try (var writer = Files.newBufferedWriter(Path.of(outPath), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
                FeatureReader<VariantContext> reader =
                        new TribbleIndexedFeatureReader<>(vcfPath, new VCFCodec(), false)) {
            // A `distilled` VCF file should have for its header all the samples in question.
            var pedigrees = Resource.getPedigrees();
            ((VCFHeader) reader.getHeader()).getGenotypeSamples().stream().forEach(sample -> {
                try {
                    // A pedigree record from the `pedigrees` table.
                    var pedigree = pedigrees.get(sample);
                    var variants = reader.iterator().stream()
                            .map(ctx -> GenotypePair
                                    .ofString(ctx.getGenotype(sample).getGenotypeString()))
                            .toList();
                    var pedRecord = new PedRecord(pedigree, variants);
                    writer.write(pedRecord.toString());
                    writer.newLine();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
            return outPath;
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    /**
     * Read the given VCF file and create the corresponding MAP file, returning their relative path.
     * 
     * @param vcfFilePath relative path to the VCF file we want to convert.
     */
    public static String toMap(String vcfFilePath) {
        // TODO: complete this method
        return vcfFilePath;
    }

    /**
     * Read the given VCF file and create the corresponding INFO file, returning its relative path.
     * 
     * @param vcfFilePath relative path to the VCF file we want to convert.
     */
    public static String toInfo(String vcfFilePath) {
        // TODO: complete this method
        return vcfFilePath;
    }
}
