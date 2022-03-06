package fr.ferret.model.conversions;

import javax.annotation.Nonnull;
import htsjdk.variant.variantcontext.VariantContext;

/** A single line in a {@code .info} file. See: https://www.cog-genomics.org/plink2/formats#info */
public record InfoRecord(@Nonnull String variantId, int position) {
    public InfoRecord(VariantContext ctx) {
        this(VcfConverter.generateVariantId(ctx), ctx.getStart());
    }

    public String toString() {
        return String.format("%s\t%d", variantId, position);
    }
}
