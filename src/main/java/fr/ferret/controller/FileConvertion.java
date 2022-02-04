package fr.ferret.controller;

import java.util.*;

/**
 * The Controler to convert files.
 */
public class FileConvertion {

    private ArrayList<String> fileExtensions;

    public FileConvertion() {
        this.fileExtensions = new ArrayList<String>();
        this.fileExtensions.add("vcf");
        this.fileExtensions.add("frq");
        this.fileExtensions.add("map");
        this.fileExtensions.add("ped");
        this.fileExtensions.add("info");
    }
}
