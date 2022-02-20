package fr.ferret.controller.settings;

/**
 * Supported versions of the genes
 */
public enum HumanGenomeVersions {
    hg19, hg38;

    @Override
    public String toString() {
        return switch (this) {
            case hg19 -> "hg19";
            case hg38 -> "hg38";
        };
    }

    public String toGRC() {
        return switch (this) {
            case hg19 -> "hg37";
            case hg38 -> "hg38";
        };
    }
}
