package fr.ferret.model.utils;

import java.util.Set;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VcfUtils {
    public static VCFHeader subVCFHeaderFromSamples(VCFHeader header, Set<String> sampleNames) {
        var meta = header.getMetaDataInInputOrder();
        return new VCFHeader(meta, sampleNames);
    }

    /**
     * Generates a new ID for a variant with an empty id {@code "."}. The generated ID will follow
     * the {@code chromosome:position} format.
     */
    public static String generateVariantId(VariantContext ctx) {
        return ctx.hasID() ? ctx.getID() : String.format("%s:%d", ctx.getContig(), ctx.getStart());
    }
}
