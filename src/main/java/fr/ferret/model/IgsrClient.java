package fr.ferret.model;

import java.io.IOException;
import java.text.MessageFormat;
import htsjdk.tribble.readers.TabixReader;
import lombok.Builder;

/**
 * Simple query client for the IGSR (International Genome Sample Resource).
 */
@Builder
public class IgsrClient {

    private int chromosome;

    @Builder.Default
    private String fileUrlFormat =
            "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/ALL.chr{0,number}.phase3_shapeit2_mvncall_integrated_v5b.20130502.genotypes.vcf.gz";

    private String getFilePath() {
        return MessageFormat.format(fileUrlFormat, chromosome);
    }

    private String getIndexPath() {
        return getFilePath() + ".tbi";
    }

    public TabixReader reader() throws IOException {
        return new TabixReader(getFilePath(), getIndexPath());
    }
}
