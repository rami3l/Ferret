package fr.ferret.controller.settings;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * The global config of Ferret
 */
@Getter
@Setter
public class FerretConfig {
    /**
     * Version du projet 1KG utilisée
     */
    private Phases1KG selectedVersion = Phases1KG.V3;

    /**
     * Selected output file type
     */
    private FileOutputType selectedOutputType = FileOutputType.ALL;

    /**
     * Selected gene version
     */
    private HumanGenomeVersions selectedHumanGenome = HumanGenomeVersions.HG19;

    /**
     * The Minor Allele Frequency
     */
    private double mafThreshold;

    //public static void saveToFile(File config) {
    //
    //}
    //
    //public static FerretConfig loadFromFile(File config) {
    //
    //}

}
