package fr.ferret.model.utils;

import java.io.File;
import java.io.OutputStream;
import java.util.stream.Stream;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder.OutputType;
import htsjdk.variant.vcf.VCFHeader;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileWriter {

    public void writeVCF(OutputStream outputStream, VCFHeader header, Stream<VariantContext> variants) {
        try (var writer = new VariantContextWriterBuilder()
                .clearOptions()
                .setOutputVCFStream(outputStream).build()) {
            writer.writeHeader(header);
            variants.forEach(writer::add);
        }
    }

    /**
     * Writes a VCF file and its associated index (idx file)
     *
     * @param outFile the file to write the VCF to
     * @param header the header to write
     * @param variants the variants to write
     */
    public void writeVCF(File outFile, VCFHeader header, Stream<VariantContext> variants) {
        writeVCF(outFile, header, variants, null);
    }

    /**
     * Writes VCF to a file (VCF type by default) and, if applicable, its index
     *
     * @param outFile the file to write the VCF to
     * @param header the header to write
     * @param variants the variants to write
     * @param outputType the type to use (VCF by default)
     */
    public void writeVCF(File outFile, VCFHeader header, Stream<VariantContext> variants, OutputType outputType) {
        if(outputType == null) {
            outputType = OutputType.VCF;
        }
        try (var writer = new VariantContextWriterBuilder()
                .setReferenceDictionary(header.getSequenceDictionary()).setOutputFile(outFile)
                .setOutputFileType(outputType).build()) {
            writer.writeHeader(header);
            variants.forEach(writer::add);
        }
    }

    public void writeVCF(String outFile, VCFHeader header, Stream<VariantContext> variants) {
        writeVCF(new File(outFile), header, variants);
    }
}
