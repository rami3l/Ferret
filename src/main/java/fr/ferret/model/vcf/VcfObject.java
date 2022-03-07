package fr.ferret.model.vcf;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;

import java.util.stream.Stream;

public record VcfObject(VCFHeader header, CloseableIterator<VariantContext> variants) {
}
