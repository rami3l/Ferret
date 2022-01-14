package fr.ferret.controller.settings;

/**
 * The global config of Ferret
 */
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

    public Phases1KG getSelectedVersion() {
        return selectedVersion;
    }

    public FileOutputType getSelectedOutputType() {
        return selectedOutputType;
    }

    public HumanGenomeVersions getSelectedHumanGenome() {
        return selectedHumanGenome;
    }

    public double getMafThreshold() {
        return mafThreshold;
    }

    public void setSelectedVersion(Phases1KG selectedVersion) {
        this.selectedVersion = selectedVersion;
    }

    public void setSelectedOutputType(FileOutputType selectedOutputType) {
        this.selectedOutputType = selectedOutputType;
    }

    public void setSelectedHumanGenome(HumanGenomeVersions selectedHumanGenome) {
        this.selectedHumanGenome = selectedHumanGenome;
    }

    public void setMafThreshold(double mafThreshold) {
        this.mafThreshold = mafThreshold;
    }
}
