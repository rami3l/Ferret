package fr.ferret.model.vcf;

import fr.ferret.model.utils.VCFHeaderExt;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;


@Getter
@AllArgsConstructor
public class VcfObject {

    private final VCFHeader header;
    private final CloseableIterator<VariantContext> variants;

    public VcfObject filter(Set<String> samples) {

        // We filter the lines (to keep only the selected populations)
        var filteredVariants = variants.stream().map(variant -> variant.subContextFromSamples(samples));

        // We filter the header
        var filteredHeader = VCFHeaderExt.subVCFHeaderFromSamples(header, samples);

        return new VcfObject(filteredHeader, new VariantIterator(filteredVariants.iterator()));

    }

    @AllArgsConstructor
    class VariantIterator implements CloseableIterator<VariantContext> {

        final Iterator<VariantContext> iterator;

        @Override public void close() {
            // TODO
        }

        @Override public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override public VariantContext next() throws NoSuchElementException {
            return iterator.next();
        }
    }
}

