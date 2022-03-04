package fr.ferret.controller.settings;

/**
 * Supported versions of the genes
 */
public enum HumanGenomeVersions {
    HG19, HG38;

    @Override
    public String toString() {
        return switch (this) {
            case HG19 -> "hg19";
            case HG38 -> "hg38";
        };
    }

    public String toGRC() {
        return switch (this) {
            case HG19 -> "hg37";
            case HG38 -> "hg38";
        };
    }
}
