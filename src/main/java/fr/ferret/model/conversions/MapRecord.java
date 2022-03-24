package fr.ferret.model.conversions;

import fr.ferret.model.utils.VcfUtils;
import htsjdk.variant.variantcontext.VariantContext;

/** A single line in a {@code .map} file. See: https://www.cog-genomics.org/plink2/formats#map */
public record MapRecord(String chromosome, String variantId, int position) {
    public MapRecord(VariantContext ctx) {
        this(ctx.getContig(), VcfUtils.generateVariantId(ctx), ctx.getStart());
    }

    public String toString() {
        return String.format("%s\t%s\t%d\t%d", chromosome, variantId, 0, position);
    }
}
