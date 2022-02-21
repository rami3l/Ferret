package fr.ferret.controller.settings;

/**
 * Supported versions of the genes
 */
public enum HumanGenomeVersions {
    hg19, hg38;

    @Override
    public String toString() {
        return switch (this) {
            case hg19 -> "GRCh37";
            case hg38 -> "GRCh38";
        };
    }
}
