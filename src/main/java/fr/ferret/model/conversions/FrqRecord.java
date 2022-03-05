package fr.ferret.model.conversions;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * A single line in a {@code .frq} file. See: https://www.cog-genomics.org/plink2/formats#frq
 */
public record FrqRecord(String chromosome, String variantId, GenotypePair genotype,
        RefFrequencyPair refFrequency) {
    public FrqRecord(VariantContext ctx, RefFrequencyPair refFrequency) {
        this(ctx.getContig(), VcfConverter.generateVariantId(ctx), GenotypePair.of(ctx),
                refFrequency);
    }

    public String toString() {
        return String.format("%s\t%s\t%s\t%s", chromosome, variantId, genotype, refFrequency);
    }
}
