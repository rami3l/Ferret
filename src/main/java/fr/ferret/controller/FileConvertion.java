package fr.ferret.controller;

import java.util.*;

/**
 * The Controler to convert files.
 */
public class FileConvertion {

    /**
     * List of the possible extentions of files, without dot.
     */
    private ArrayList<String> fileExtensions;

    public FileConvertion() {
        this.fileExtensions = new ArrayList<String>();
        this.fileExtensions.add("vcf");
        this.fileExtensions.add("frq");
        this.fileExtensions.add("map");
        this.fileExtensions.add("ped");
        this.fileExtensions.add("info");
    }

    /**
     * Read the given VCF file and create the corresponding FRQ file, returning its relative path.
     * 
     * @param vcfFilePath relative path to the VCF file we want to convert.
     */
    public String convertVcfToFrq(String vcfFilePath) {
        // TODO.
        return vcfFilePath;
    }

    /**
     * Read the given VCF file and create the corresponding MAP and PED files, returning their
     * relative path.
     * 
     * @param vcfFilePath relative path to the VCF file we want to convert.
     */
    public String convertVcfToMapAndPed(String vcfFilePath) {
        // TODO.
        return vcfFilePath;
    }

    /**
     * Read the given VCF file and create the corresponding INFO file, returning its relative path.
     * 
     * @param vcfFilePath relative path to the VCF file we want to convert.
     */
    public String convertVcfToInfo(String vcfFilePath) {
        // TODO.
        return vcfFilePath;
    }
}
