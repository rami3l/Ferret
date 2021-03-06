package fr.ferret.model.vcf;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import htsjdk.tribble.FeatureReader;
import htsjdk.tribble.TabixFeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import reactor.core.publisher.Mono;

/**
 * Simple query client for the IGSR (International Genome Sample Resource).
 */
public class IgsrClient implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(IgsrClient.class.getName());

    /**
     * Cached readers for each chromosome (to avoid downloading multiple times the same vcf header
     * during one Ferret Request which may contains multiple genes/variants on the same chromosome)
     */
    private final Map<String, FeatureReader<VariantContext>> readers = new HashMap<>();

    /**
     * The vcf url template
     */
    private final String urlTemplate;

    /**
     * Creates a client for the IGSR (International Genome Sample Resource)
     *
     * @param urlTemplate The template to use for creating the url from the chromosome.
     *                    `{0}` will be replaced by the chromosome.
     */
    public IgsrClient(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    private String getFilePath(String chromosome) {
        // Replaces the chromosome in the template string.
        return MessageFormat.format(urlTemplate, chromosome);
    }

    /**
     * Connects to the IGSR database and initializes a VCF file reader. The VCF file reader is saved
     * in the $reader attribute
     *
     * @return a {@link TabixFeatureReader} instance pointing to the given file path
     */
    private FeatureReader<VariantContext> initReader(String chromosome) throws IOException {
        logger.log(Level.INFO, "Downloader VCF header for chr {0}", chromosome);
        // If index path is not filePath + .tbi, we can add a parameter to TabixFetureReader
        readers.put(chromosome, new TabixFeatureReader<>(getFilePath(chromosome), new VCFCodec()));
        return readers.get(chromosome);
    }

    /**
     * Initializes the VCF file reader if needed and returns it. The reader is initialized lazily
     * (you need to subscribe to the Mono)
     *
     * @return a {@link Mono} encapsulating the reader
     */
    public Mono<FeatureReader<VariantContext>> getReader(String chromosome) {
        var reader = readers.get(chromosome);
        return Mono.fromCallable(() -> reader == null ? initReader(chromosome) : reader);
    }

    /**
     * Closes all the VCF readers created by this IgsrClient. You should call this method at the end
     * of the download to avoid memory leaks (but not before the end of the download).
     */
    public void close() {
        readers.values().forEach(reader -> {
            try {
                reader.close();
            } catch (Exception e) {
                logger.warning("Failed to close a VCF reader");
            }
        });
        readers.clear();
        logger.info("IgsrClient closed");
    }

}
