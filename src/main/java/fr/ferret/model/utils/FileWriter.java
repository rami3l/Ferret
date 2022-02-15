package fr.ferret.model.utils;

import java.io.File;
import java.io.OutputStream;
import java.util.stream.Stream;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFHeader;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileWriter {

    public void writeVCF(OutputStream outputStream, VCFHeader header, Stream<VariantContext> contexts) {
        try (var writer = new VariantContextWriterBuilder()
                .clearOptions()
                .setOutputVCFStream(outputStream).build()) {
            writer.writeHeader(header);
            contexts.forEach(writer::add);
        }
    }

    public void writeVCF(File outFile, VCFHeader header, Stream<VariantContext> contexts) {
        try (var writer = new VariantContextWriterBuilder()
                .setReferenceDictionary(header.getSequenceDictionary()).setOutputFile(outFile)
                .setOutputFileType(VariantContextWriterBuilder.OutputType.VCF).build()) {
            writer.writeHeader(header);
            contexts.forEach(writer::add);
        }
    }

    public void writeVCF(String outFile, VCFHeader header, Stream<VariantContext> contexts) {
        writeVCF(new File(outFile), header, contexts);
    }
}
