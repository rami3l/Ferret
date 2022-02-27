package fr.ferret.model;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;
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
     * @return a {@code TabixFeatureReader} instance pointing to the given file path
     * @throws IOException
     */
    public FeatureReader<VariantContext> reader() throws IOException {
        logger.info("Initializing reader...");
        return new TabixFeatureReader<>(getFilePath(), getIndexPath(), new VCFCodec());
    }

    /**
     * Exports a "distilled" VCF file from an IGSR online database query.
     * 
     * @param outFile the output {@code File}
     * @param start the starting index of the query
     * @param end the ending index of the query
     * @param phase the 1000 Genome project phase of this query
     * @param selection the selected populations
     */
    public void exportVCFFromSamples(File outFile, int start, int end, Phases1KG phase,
            ZoneSelection selection) throws IOException {
        exportVCFFromSamples(outFile, start, end, Resource.getSamples(phase, selection));
    }

    /**
     * Exports a "distilled" VCF file from an IGSR online database query.
     * 
     * @param outFile the output {@code File}
     * @param start the starting index of the query
     * @param end the ending index of the query
     * @param samples the sample names, eg. {HG00096, HG0009}
     */
    public void exportVCFFromSamples(File outFile, int start, int end, Set<String> samples)
            throws IOException {
        try (var reader = this.reader(); var lines = reader.query(chromosome, start, end)) {
            var contexts = lines.stream().map(context -> context.subContextFromSamples(samples));
            var header =
                    VCFHeaderExt.subVCFHeaderFromSamples((VCFHeader) reader.getHeader(), samples);
            FileWriter.writeVCF(outFile, header, contexts);
        }
    }
}
