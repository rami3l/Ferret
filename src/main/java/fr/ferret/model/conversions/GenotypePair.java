package fr.ferret.model.conversions;

import java.util.List;

public record GenotypePair(String reference, String alternates) {
    public static GenotypePair ofGenotypeString(String genotypeString) {
        // Split `|` or `/`.
        var alleles = genotypeString.split("\\Q[|/]\\E");
        return new GenotypePair(alleles[0], alleles[1]);
    }

    public List<String> toList() {
        return List.of(reference, alternates);
    }
}
