package fr.ferret.model;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Logger;
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.utils.Resource;
import htsjdk.tribble.FeatureReader;
import htsjdk.tribble.TabixFeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import lombok.Builder;

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
     * The start position (default: 0)
     */
    @Builder.Default
    private int start = 0;

    /**
     * The end position (default: infini)
     */
    @Builder.Default
    private int end = Integer.MAX_VALUE;

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

    /** Returns a new {@code TabixFeatureReader} instance pointing to the given file path. */
    public FeatureReader<VariantContext> reader() throws IOException {
        logger.info("Initializing reader...");
        return new TabixFeatureReader<>(getFilePath(), getIndexPath(), new VCFCodec());
    }
}
