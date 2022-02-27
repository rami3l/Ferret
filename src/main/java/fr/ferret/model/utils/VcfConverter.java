package fr.ferret.model.utils;

import java.util.List;
import lombok.experimental.UtilityClass;

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
     * Read the given VCF file and create the corresponding MAP and PED files, returning their
     * relative path.
     * 
     * @param vcfFilePath relative path to the VCF file we want to convert.
     */
    public static String toMapPed(String vcfFilePath) {
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
