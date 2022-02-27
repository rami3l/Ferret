package fr.ferret.model.utils;

import java.util.Set;
import htsjdk.variant.vcf.VCFHeader;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VCFHeaderExt {
    public static VCFHeader subVCFHeaderFromSamples(VCFHeader header, Set<String> sampleNames) {
        var meta = header.getMetaDataInInputOrder();
        return new VCFHeader(meta, sampleNames);
    }
}
