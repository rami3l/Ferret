package fr.ferret.model.conversions;

import java.security.InvalidParameterException;
import java.util.List;

public record GenotypePair(String reference, String alternates) {
    public static GenotypePair ofString(String genotypeString) {
        // Split `|` or `/`.
        var alleles = genotypeString.split("[\\|\\/]");
        if (alleles.length < 2) {
            throw new InvalidParameterException(
                    "The genotype string `" + genotypeString + "` has less than 2 alleles");
        }
        return new GenotypePair(alleles[0], alleles[1]);
    }

    public List<String> toList() {
        return List.of(reference, alternates);
    }
}
