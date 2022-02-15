package fr.ferret.model.utils;

import java.util.List;

/**
 * The Controler to convert files.
 */
public class FileConverter {

    /**
     * List of the possible file extentions, without dot.
     */
    private static final List<String> fileExtensions = List.of("vcf", "frq", "map", "ped", "info");

    private FileConverter() {
        // Util classes shouldn't be instanciated
    }

    /**
     * Read the given VCF file and create the corresponding FRQ file, returning its relative path.
     * 
     * @param vcfFilePath relative path to the VCF file we want to convert.
     */
    public static String convertVcfToFrq(String vcfFilePath) {
        // TODO: complete this method
        return vcfFilePath;
    }

    /**
     * Read the given VCF file and create the corresponding MAP and PED files, returning their
     * relative path.
     * 
     * @param vcfFilePath relative path to the VCF file we want to convert.
     */
    public static String convertVcfToMapAndPed(String vcfFilePath) {
        // TODO: complete this method
        return vcfFilePath;
    }

    /**
     * Read the given VCF file and create the corresponding INFO file, returning its relative path.
     * 
     * @param vcfFilePath relative path to the VCF file we want to convert.
     */
    public static String convertVcfToInfo(String vcfFilePath) {
        // TODO: complete this method
        return vcfFilePath;
    }
}
