package fr.ferret.model.utils;

import java.io.File;
import java.util.stream.Stream;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFHeader;

public class FileWriter {

    private FileWriter() {
        // util classes should not be implemented
    }

    public static void writeVCF(File outFile, VCFHeader header, Stream<VariantContext> contexts) {
        var writer = new VariantContextWriterBuilder()
                .setReferenceDictionary(header.getSequenceDictionary()).setOutputFile(outFile)
                .setOutputFileType(VariantContextWriterBuilder.OutputType.VCF).build();
        writer.writeHeader(header);
        contexts.forEach(writer::add);
        writer.close();
    }
}
