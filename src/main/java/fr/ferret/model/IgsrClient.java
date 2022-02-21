package fr.ferret.model;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import fr.ferret.controller.exceptions.runtime.ResourceAccessException;
import fr.ferret.controller.exceptions.runtime.VCFReaderInitializationException;
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.model.utils.FileWriter;
import fr.ferret.model.utils.VCFHeaderExt;
import fr.ferret.utils.Resource;
import htsjdk.tribble.FeatureReader;
import htsjdk.tribble.TabixFeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder.OutputType;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;
import lombok.Builder;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Simple query client for the IGSR (International Genome Sample Resource).
 */
@Builder
public class IgsrClient {

    private static final Logger logger = Logger.getLogger(IgsrClient.class.getName());

    /**
     * The chromosome number, eg. `1`.
     */
    private final String chromosome;

    /**
     * The phase to use for getting variants (default: selected version)
     */
    @Builder.Default
    private final Phases1KG phase1KG = Resource.CONFIG.getSelectedVersion();

    /**
     * The vcf url template. `{0}` will be replaced by the chromosome.
     */
    @Builder.Default
    private final String urlTemplate = Resource.getVcfUrlTemplate(Resource.CONFIG.getSelectedVersion());

    @Builder.Default
    private final OutputType outputType = OutputType.VCF;

    // Attribute name starting with `$` to be excluded from the builder
    private FeatureReader<VariantContext> $reader;


    private String getFilePath() {
        // Replace chromosome in the template string.
        return MessageFormat.format(urlTemplate, chromosome);
    }

    /**
     * Connects to the IGSR database and initializes a VCF file reader.
     * The VCF file reader is saved in the $reader attribute
     *
     * @return a {@link TabixFeatureReader} instance pointing to the given file path
     */
    private FeatureReader<VariantContext> initReader() throws IOException {
        logger.info("Initializing reader...");
        $reader = new TabixFeatureReader<>(getFilePath(), new VCFCodec());
        return $reader;
    }

    /**
     * Initializes the VCF file reader if needed and returns it.
     * The reader is initialized lazily (you need to subscribe to the Mono)
     *
     * @return a {@link Mono<FeatureReader<VariantContext>>} encapsulating the reader
     */
    public Mono<FeatureReader<VariantContext>> getReader() {
        return Mono.fromCallable(() -> $reader == null ? initReader() : $reader);
    }

    /**
     * Exports a "distilled" VCF file from an IGSR online database query.
     * 
     * @param outFile the output {@link File}
     * @param start the starting index of the query
     * @param end the ending index of the query
     * @param selection the selected populations
     * @return {@link Disposable} linked to the launched task
     */
    public Disposable exportVCFFromSamples(File outFile, int start, int end, ZoneSelection selection) {
        logger.info("Exporting...");
        try {
            var samples = Resource.getSamples(phase1KG, selection);
            return exportVCFFromSamples(outFile, start, end, samples);
        } catch (IOException e) {
            throw new ResourceAccessException(e);
        }
    }

    /**
     * Exports a "distilled" VCF file from an IGSR online database query.
     * 
     * @param outFile the output {@link File}
     * @param start the starting index of the query
     * @param end the ending index of the query
     * @param samples the sample names, eg. {HG00096, HG0009}
     * @return {@link Disposable} linked to the launched task
     */
    public Disposable exportVCFFromSamples(File outFile, int start, int end, Set<String> samples) {
        return getReader().subscribeOn(Schedulers.boundedElastic())
            .doOnNext(reader -> {
                logger.info("Reading lines from remote vcf file...");
                try {
                    var lines = reader.query(chromosome, start, end);
                    var variants = lines.stream().map(variant -> variant.subContextFromSamples(samples));
                    var header = VCFHeaderExt.subVCFHeaderFromSamples((VCFHeader) reader.getHeader(), samples);
                    logger.info("Writing to disk...");
                    FileWriter.writeVCF(outFile, header, variants, outputType);
                    reader.close();
                    logger.info(String.format("%s file written", outFile.getName()));
                } catch (IOException e) {
                    throw new ResourceAccessException(e);
                }
            })
            .doOnError(e -> {throw new VCFReaderInitializationException(e);})
            .subscribe();
    }
}
