package fr.ferret.model.utils;

import java.io.File;
import java.io.OutputStream;
import java.util.stream.Stream;

import fr.ferret.utils.Resource;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder.OutputType;
import htsjdk.variant.vcf.VCFHeader;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileWriter {

    public enum VCFOutputType {
        VCF, BCF, VCF_GZ
    }

    // TODO: should we add a writeVCF(OutputStream, Stream<VariantContext>) method? It could be useful if we need VCF without header for conversion

    /**
     * Writes a VCF to an {@link OutputStream outputStream}
     *
     * @param outputStream The {@link OutputStream} to write the VCF to
     * @param header the {@link VCFHeader header} to write
     * @param variants The variants of the VCF
     */
    public void writeVCF(OutputStream outputStream, VCFHeader header, Stream<VariantContext> variants) {
        try (var writer = new VariantContextWriterBuilder()
                .clearOptions()
                .setOutputVCFStream(outputStream).build()) {
            writer.writeHeader(header);
            variants.forEach(writer::add);
        }
    }

    /**
     * Writes a VCF to a {@link File file}
     * TODO: add Ferret Settings to determine format to use (VCF/BCF) and if index file must be written
     *
     * @param outFile the {@link File file} to write the VCF to
     * @param header the {@link VCFHeader header} to write
     * @param variants the variants to write
     */
    public void writeVCF(File outFile, VCFHeader header, Stream<VariantContext> variants) {

        var outputType = switch (Resource.VCF_OUTPUT_TYPE) {
            case VCF_GZ -> OutputType.BLOCK_COMPRESSED_VCF;
            case BCF -> OutputType.BCF;
            case VCF -> OutputType.VCF;
        };
        boolean writeIndex = Resource.WRITE_VCF_INDEX;

        writeVCF(outFile, header, variants, outputType, writeIndex);
    }

    /**
     * Writes VCF to a file (VCF type by default) and, if applicable, its index
     *
     * @param outFile the file to write the VCF to
     * @param header the header to write
     * @param variants the variants to write
     * @param outputType the type to use (VCF by default)
     * @param writeIndex Boolean indicating if index file must be written
     */
    private void writeVCF(File outFile, VCFHeader header, Stream<VariantContext> variants, OutputType outputType, boolean writeIndex) {
        // TODO: Can we get a sequence dictionary from another endpoint (if it is not present in the header) ?
        if(writeIndex && outputType!=OutputType.BLOCK_COMPRESSED_VCF && header.getSequenceDictionary()==null) {
            // TODO: indicate that it's not possible to create the index file because the header doesn't contain a sequence dictionary
            writeIndex = false;
        }
        var writerBuilder = new VariantContextWriterBuilder().modifyOption(Options.INDEX_ON_THE_FLY, writeIndex);
        if(writeIndex) {
            writerBuilder.setReferenceDictionary(header.getSequenceDictionary());
        }
        try (var writer = writerBuilder.setOutputFile(outFile).setOutputFileType(outputType).build()) {
            writer.writeHeader(header);
            variants.forEach(writer::add);
        }
    }

}
