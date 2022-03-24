package fr.ferret.model.conversions;

import fr.ferret.model.utils.VcfUtils;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * A single line in a {@code .frq} file. See: https://www.cog-genomics.org/plink2/formats#frq
 */
public record FrqRecord(String chromosome, String variantId, GenotypePair genotype,
        RefFrequencyPair refFrequency) {
    public FrqRecord(VariantContext ctx) {
        this(ctx.getContig(), VcfUtils.generateVariantId(ctx), GenotypePair.of(ctx),
                RefFrequencyPair.of(ctx));
    }

    public String toString() {
        return String.format("%s\t%s\t%s\t%s", chromosome, variantId, genotype, refFrequency);
    }
}
