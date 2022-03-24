package fr.ferret.model.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import com.pivovarit.function.ThrowingConsumer;
import fr.ferret.model.conversions.*;
import fr.ferret.model.vcf.VcfObject;
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

    /**
     * Writes a VCF to a {@link File file}
     *
     * @param vcf The {@link VcfObject} to write
     * @param outFile the {@link File file} to write the VCF to
     */
    public void writeVCF(VcfObject vcf, String outFile) {

        var outputType = switch (Resource.VCF_OUTPUT_TYPE) {
            case VCF_GZ -> OutputType.BLOCK_COMPRESSED_VCF;
            case BCF -> OutputType.BCF;
            case VCF -> OutputType.VCF;
        };
        boolean writeIndex = Resource.WRITE_VCF_INDEX;

        writeVCF(new File(outFile), vcf.getHeader(), vcf.getVariants().stream(), outputType, writeIndex);
    }

    /**
     * Writes a Frq file from a {@link VcfObject}.
     *  @param vcf The {@link VcfObject} to write
     * @param outPath The path to write the Frq file to
     */
    public void writeFRQ(VcfObject vcf, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath)) {
            vcf.getVariants().stream().forEach(ThrowingConsumer.unchecked(variant -> {
                var rec = new FrqRecord(variant);
                writer.write(rec.toString());
                writer.newLine();
            }));
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    /**
     * Writes a Ped file from a {@link VcfObject}.
     *  @param vcf The {@link VcfObject} to write
     * @param outPath The path to write the Ped file to
     */
    public void writePED(VcfObject vcf, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath)) {
            // A `distilled` VCF file should have for its header all the samples in question.
            var pedigrees = Resource.getPedigrees();
            var variantList = vcf.getVariants().toList();
            vcf.getHeader().getGenotypeSamples()
                .forEach(ThrowingConsumer.unchecked(sample -> {
                    // A pedigree record from the `pedigrees` table.
                    var pedigree = pedigrees.get(sample);
                    var variants = variantList.stream()
                        .map(ctx -> GenotypePair.of(ctx, sample)).toList();
                    var pedRecord = new PedRecord(pedigree, variants);
                    writer.write(pedRecord.toString());
                    writer.newLine();
                }));
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    /**
     * Writes a Map file from a {@link VcfObject}.
     *  @param vcf The {@link VcfObject} to write
     * @param outPath The path to write the Map file to
     */
    public void writeMAP(VcfObject vcf, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath);) {
            for (var variant : vcf.getVariants().toList()) {
                writer.write(new MapRecord(variant).toString());
                writer.newLine();
            }
        }
    }

    /**
     * Writes an Info file from a {@link VcfObject}.
     *  @param vcf The {@link VcfObject} to write
     * @param outPath The path to write the Info file to
     */
    public void writeINFO(VcfObject vcf, String outPath) throws IOException {
        try (var writer = truncatingFileWriter(outPath)) {
            for (var variant : vcf.getVariants().toList()) {
                writer.write(new InfoRecord(variant).toString());
                writer.newLine();
            }
        }
    }
    

    private BufferedWriter truncatingFileWriter(String outPath) throws IOException {
        return Files.newBufferedWriter(Path.of(outPath), StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
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
            // TODO: ExceptionHandler → indicate that it's not possible to create the index file because the header doesn't contain a sequence dictionary
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
