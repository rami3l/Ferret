package fr.ferret.model.utils;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFHeader;

import java.util.stream.Stream;

public class FileWriter {

    private FileWriter() {
        // util classes should not be implemented
    }

    public static void writeVCF(String filename, VCFHeader header, Stream<VariantContext> contexts) {
        var writer = new VariantContextWriterBuilder()
                .setReferenceDictionary(header.getSequenceDictionary())
                .setOutputFile(filename)
                .setOutputFileType(VariantContextWriterBuilder.OutputType.VCF).build();
        writer.writeHeader(header);
        contexts.forEach(writer::add);
        writer.close();
    }
}
