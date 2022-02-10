package fr.ferret.model;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.utils.Resource;
import htsjdk.tribble.readers.TabixReader;
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

    /** Returns a new {@code TabixReader} instance pointing to the given file path. */
    public TabixReader reader() throws IOException {
        logger.info("Initializing reader...");
        return new TabixReader(getFilePath(), getIndexPath());
    }

    /**
     * Get genotype from all population for the selected chromosome, between the select start and
     * end positions
     * 
     * @return A list of the lines, each line corresponding to a chromose position (variant) and
     *         being a list of elements (chromosome, position, etc.)
     */
    public List<List<String>> getAllPopulations() {
        logger.info("getting all population...");
        List<List<String>> result = new ArrayList<>();
        try (var reader = reader();) {
            logger.info("reader started");
            var it = reader.query(chromosome, start, end);
            String line;
            while ((line = it.next()) != null) {
                logger.info("getting one line");
                result.add(Arrays.asList(line.split("\\s+")));
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Vcf access error:", e);
        }
        return result;
    }
}
