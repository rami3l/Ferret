package fr.ferret.model.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import fr.ferret.utils.Resource;
import htsjdk.tribble.TabixFeatureReader;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;
import lombok.experimental.UtilityClass;
import picard.pedigree.PedFile;
import picard.pedigree.Sex;

/**
 * The util class to convert `.vcf` files.
 */
@UtilityClass
public class VcfConverter {

    /**
     * List of the possible file extentions, without dot.
     */
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
        try (var reader = new TabixFeatureReader<>(vcfPath, new VCFCodec())) {
            // A `distilled` VCF file should have for its header all the samples in question.
            var ped = new PedFile(true);
            var pedigrees = Resource.getPedigrees();
            ((VCFHeader) reader.getHeader()).getGenotypeSamples().stream().forEach(sample -> {
                // The Record instance from the `pedigrees` table.
                var r = pedigrees.get(sample);
                var trio = ped.new PedTrio(r.getFamilyId(), r.getIndividualId(), r.getPaternalId(),
                        r.getMaternalId(), Sex.fromCode(r.getGender()), r.getPhenotype());
                ped.add(trio);
            });
            ped.write(new File(outPath));
            return outPath;
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
