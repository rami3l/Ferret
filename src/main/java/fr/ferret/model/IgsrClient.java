package fr.ferret.model;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import htsjdk.tribble.readers.TabixReader;
import lombok.Builder;

/**
 * Simple query client for the IGSR (International Genome Sample Resource).
 */
@Builder
public class IgsrClient {

    private static final Logger logger = Logger.getLogger(IgsrClient.class.getName());

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

    // TODO: Change this to constant aliases.
    @Builder.Default
    private String fileUrlFormat =
            "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/ALL.chr{0}.phase3_shapeit2_mvncall_integrated_v5b.20130502.genotypes.vcf.gz";

    private String getFilePath() {
        // Replace chromosome number in the template string.
        return MessageFormat.format(fileUrlFormat, chromosome);
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
