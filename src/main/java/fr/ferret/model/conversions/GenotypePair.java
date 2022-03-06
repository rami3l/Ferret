package fr.ferret.model.conversions;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * The {@code REF/ALT} pair representing a genotype (eg. {@code A|G,AGGGAGG}), such as that in a
 * 1000 Genomes Extended VCF file. See:
 * https://gatk.broadinstitute.org/hc/en-us/articles/360035531692-VCF-Variant-Call-Format
 */
public record GenotypePair(String reference, String alternates) {
    /**
     * Returns a new {@link GenotypePair} using a genotype String (eg. {@code A|G,AGGGAGG})
     * separated by {@code '/'} or {@code '|'}.
     */
    public static GenotypePair of(String genotypeString) {
        var alleles = genotypeString.split("[\\|\\/]");
        if (alleles.length < 2) {
            throw new InvalidParameterException(
                    "The genotype string `" + genotypeString + "` has less than 2 alleles");
        }
        return new GenotypePair(alleles[0], alleles[1]);
    }

    /**
     * Returns a new {@link GenotypePair} using the genotype indicated by the tuple
     * ({@link VariantContext}, sample).
     */
    public static GenotypePair of(VariantContext ctx, String sample) {
        return GenotypePair.of(ctx.getGenotype(sample).getGenotypeString());
    }

    /**
     * Returns a new {@link GenotypePair} using the variant's {@code REF/ALT} fields. Used mainly by
     * {@link FrqRecord}.
     */
    public static GenotypePair of(VariantContext ctx) {
        return GenotypePair.of(ctx.getReference(), ctx.getAlternateAlleles());
    }

    public static GenotypePair of(Allele references, List<Allele> alternates) {
        var refStr = references.getBaseString();
        var altsStr = alternates.isEmpty() ? "."
                : alternates.stream().map(Allele::getBaseString).collect(Collectors.joining(","));
        return new GenotypePair(refStr, altsStr);
    }

    public List<String> toListString() {
        return List.of(reference, alternates);
    }

    public String toString() {
        return String.join("\t", toListString());
    }
}
