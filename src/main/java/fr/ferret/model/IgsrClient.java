package fr.ferret.model;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import fr.ferret.controller.exceptions.ExceptionHandler;
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
import reactor.core.publisher.Flux;
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
     * @return a {@link Mono} encapsulating the reader
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
     * @return {@link Flux} of {@link String strings} indicating the progress of the treatment
     */
    public Flux<String> exportVCFFromSamples(File outFile, int start, int end, ZoneSelection selection) {
        try {
            var samples = Resource.getSamples(phase1KG, selection);
            return exportVCFFromSamples(outFile, start, end, samples);
        } catch (IOException e) {
            ExceptionHandler.ressourceAccessError(e);
            return Flux.error(e);
        }
    }

    /**
     * Exports a "distilled" VCF file from an IGSR online database query.
     *
     * @param outFile the output {@link File}
     * @param start the starting index of the query
     * @param end the ending index of the query
     * @param samples the sample names, e.g. {HG00096, HG0009}
     * @return {@link Flux} of {@link String strings} indicating the progress of the treatment
     */
    public Flux<String> exportVCFFromSamples(File outFile, int start, int end, Set<String> samples) {
        return Flux.create(state -> {
                // We initialize the reader (download of the VCF header)
                state.next(State.DOWNLOADING_HEADER);
                getReader().subscribeOn(Schedulers.boundedElastic()).doOnNext(reader -> {

                    try {
                        // We download the lines from start to end positions
                        state.next(State.DOWNLOADING_LINES);
                        logger.info("Downloading VCF lines...");
                        var lines = reader.query(chromosome, start, end);

                        // We filter the lines (to keep only the selected populations)
                        var variants = lines.stream().map(variant -> variant.subContextFromSamples(samples));

                        // We filter the header
                        var header =
                            VCFHeaderExt.subVCFHeaderFromSamples((VCFHeader) reader.getHeader(), samples);

                        // We write the VCF file
                        state.next(State.WRITING);
                        logger.info("Writing to disk...");
                        FileWriter.writeVCF(outFile, header, variants, outputType);

                        // The download is ok
                        state.next(String.format(State.WRITTEN, outFile.getName()));
                        logger.info(String.format("%s file written", outFile.getName()));
                        state.complete();
                        reader.close();

                        // We catch exception which could happen with the use of the reader
                        // TODO: catch FileSystemException
                    } catch (IOException e) {
                        ExceptionHandler.vcfStreamingError(e);
                        state.error(e);
                    }
                }).doOnError(ExceptionHandler::connectionError)
                    .doOnError(state::error)
                    .onErrorStop().subscribe();
            }
        );
    }
}
