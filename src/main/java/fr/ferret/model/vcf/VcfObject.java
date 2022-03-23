package fr.ferret.model.vcf;

import fr.ferret.model.utils.VCFHeaderExt;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.DelegatingIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import lombok.Getter;

import java.util.Iterator;
import java.util.Set;


@Getter
public class VcfObject {

    private final VCFHeader header;
    private final CloseableIterator<VariantContext> variants;

    public VcfObject(VCFHeader header, Iterator<VariantContext> variants) {
        this.header = header;
        this.variants = new DelegatingIterator<>(variants);
    }

    public VcfObject filter(Set<String> samples) {

        // We filter the lines (to keep only the selected populations)
        var filteredVariants = variants.stream().map(variant -> variant.subContextFromSamples(samples));

        // We filter the header
        var filteredHeader = VCFHeaderExt.subVCFHeaderFromSamples(header, samples);

        return new VcfObject(filteredHeader, filteredVariants.iterator());

    }

}

