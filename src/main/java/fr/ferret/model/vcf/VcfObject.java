package fr.ferret.model.vcf;

import fr.ferret.model.utils.VcfUtils;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.DelegatingIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import lombok.Getter;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class VcfObject {

    @Getter
    private final VCFHeader header;
    private final CloseableIterator<VariantContext> variants;
    private List<VariantContext> backedVariants;

    public VcfObject(VCFHeader header, Iterator<VariantContext> variants) {
        this.header = header;
        this.variants = new DelegatingIterator<>(variants);
    }

    public void backUp() {
        if(backedVariants == null) {
            backedVariants = variants.toList();
            variants.close();
        }
    }

    public CloseableIterator<VariantContext> getVariants() {
        return backedVariants == null ? variants : new DelegatingIterator<>(backedVariants.iterator());
    }

    public VcfObject filter(Set<String> samples) {

        // We filter the lines (to keep only the selected populations)
        var filteredVariants = variants.stream().map(variant -> variant.subContextFromSamples(samples));

        // We filter the header
        var filteredHeader = VcfUtils.subVCFHeaderFromSamples(header, samples);

        return new VcfObject(filteredHeader, filteredVariants.iterator());
    }

}


