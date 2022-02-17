package fr.ferret.model;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.model.utils.FileWriter;
import fr.ferret.model.utils.VCFHeaderExt;
import fr.ferret.utils.Resource;
import htsjdk.tribble.FeatureReader;
import htsjdk.tribble.TabixFeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
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
    private static final String HOST = Resource.getServerConfig("1kg.host") + "/";

    /**
     * The chromosome number, eg. `1`.
     */
    private String chromosome;

    /**
     * The phase to use for getting variants (default: selected version)
     */
    @Builder.Default
    private Phases1KG phase1KG = Resource.CONFIG.getSelectedVersion();

    private String getFilePath() {
        String phase = Resource.getPhase(phase1KG);
        String path = Resource.getServerConfig("1kg." + phase + ".path");
        String filenameTemplate = Resource.getServerConfig("1kg." + phase + ".filename");
        // Replace chromosome in the template string.
        return HOST + path + "/" + MessageFormat.format(filenameTemplate, chromosome);
    }

    private String getIndexPath() {
        return getFilePath() + ".tbi";
    }

    /**
     * Connect to the IGSR database and launch a VCF file reader.
     * 
     * @return a {@link TabixFeatureReader} instance pointing to the given file path
     */
    public Mono<FeatureReader<VariantContext>> getReader() {
        logger.info("Initializing reader...");
        return Mono.fromCallable(() -> new TabixFeatureReader<>(getFilePath(), getIndexPath(), new VCFCodec()));
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
    public Disposable exportVCFFromSamples(File outFile, int start, int end, ZoneSelection selection)
            throws IOException {
        logger.info("Exporting...");
        return exportVCFFromSamples(outFile, start, end, Resource.getSamples(phase1KG, selection));
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
                    FileWriter.writeVCF(outFile, header, variants);
                    reader.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to get vcf file", e);
                }
            }).subscribe();
    }
}
