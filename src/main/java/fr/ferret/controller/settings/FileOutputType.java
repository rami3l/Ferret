package fr.ferret.controller.settings;

import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Output file types
 */
public enum FileOutputType {
    ALL, FRQ, VCF;

    @AllArgsConstructor
    public enum Extension {
        VCF("vcf"), FRQ("frq"), MAP("map"), PED("ped"), INFO("info");

        private final String ext;

        @Override public String toString() {
            return ext;
        }
    }

    public List<Extension> extensions() {
        return switch(this) {
            case ALL -> List.of(Extension.FRQ, Extension.MAP, Extension.PED, Extension.INFO);
            case FRQ -> List.of(Extension.FRQ);
            case VCF -> List.of(Extension.VCF);
        };
    }
}
