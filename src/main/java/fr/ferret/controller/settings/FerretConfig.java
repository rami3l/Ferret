package fr.ferret.controller.settings;

import lombok.Getter;
import lombok.Setter;

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
    private HumanGenomeVersions selectedHumanGenome = HumanGenomeVersions.hg19;

    /**
     * The Minor Allele Frequency
     */
    private double mafThreshold;

}
