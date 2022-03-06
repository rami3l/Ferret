package fr.ferret.model.conversions;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

public record GenotypePair(String reference, String alternates) {
    public static GenotypePair of(String genotypeString) {
        // Split `|` or `/`.
        var alleles = genotypeString.split("[\\|\\/]");
        if (alleles.length < 2) {
            throw new InvalidParameterException(
                    "The genotype string `" + genotypeString + "` has less than 2 alleles");
        }
        return new GenotypePair(alleles[0], alleles[1]);
    }

    public static GenotypePair of(VariantContext ctx, String sample) {
        return GenotypePair.of(ctx.getGenotype(sample).getGenotypeString());
    }

    public static GenotypePair of(VariantContext ctx) {
        var ref = ctx.getReference().getBaseString();
        var alts = ctx.getAlternateAlleles();
        var altsStr = alts.isEmpty() ? "."
                : alts.stream().map(Allele::getBaseString).collect(Collectors.joining(","));
        return new GenotypePair(ref, altsStr);
    }

    public List<String> toListString() {
        return List.of(reference, alternates);
    }

    public String toString() {
        return String.join("\t", toListString());
    }
}
