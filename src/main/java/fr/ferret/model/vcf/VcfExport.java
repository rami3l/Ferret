package fr.ferret.model.vcf;

import com.pivovarit.function.ThrowingFunction;
import fr.ferret.controller.exceptions.VcfStreamingException;
import fr.ferret.model.SampleSelection;
import fr.ferret.model.locus.Locus;
import fr.ferret.model.state.PublishingStateProcessus;
import fr.ferret.model.state.State;
import fr.ferret.model.utils.FileWriter;
import fr.ferret.utils.Resource;
import htsjdk.samtools.util.MergingIterator;
import htsjdk.tribble.FeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * An object representing a VCF export. It is constructed with a {@link Locus} {@link Flux}. We can
 * set a {@link SampleSelection population filter} with the <i>setFilter</i> method. We start the
 * export with the <i>start</i> method, passing it the {@link File outFile}
 */
public class VcfExport extends PublishingStateProcessus<Void> {

    private static final Logger logger = Logger.getLogger(VcfExport.class.getName());

    /** When error while contacting the server, we retry several times */
    private static final int NB_RETRY = 3;
    private static final Duration RETRY_DELAY = Duration.ofMillis(500);

    /**
     * The individuals to keep in the VCF file. If null, no population filter will be performed
     */
    private Set<String> samples;

    /**
     * The client used to download the vcf. It uses the url template for the selected phase
     */
    private final IgsrClient client =
        new IgsrClient(Resource.getVcfUrlTemplate(Resource.config().getSelectedPhase()));

    /**
     * Constructs a {@link VcfExport}. It is used to export a "distilled" VCF file from an IGSR
     * online database query.<br>
     * Call the {@link VcfExport#setFilter(SampleSelection)} method to filter the VCF by populations
     *
     * @param locusList The {@link List} of {@link Locus} to export in a VCF file.
     * @param outFile the output {@link File}
     */
    public VcfExport(List<Locus> locusList, File outFile) {
        resultPromise = getVcf(locusList).subscribeOn(Schedulers.boundedElastic())
            .collect(Collectors.toList())
            .flatMap(this::merge).doOnNext(vcf -> {
                if (samples != null) {
                    logger.info("Filtering by samples");
                    vcf = vcf.filter(samples);
                }
                publishState(State.writing(outFile.getName()));
                logger.info("Writing to disk...");
                FileWriter.writeVCF(outFile, vcf.getHeader(), vcf.getVariants().stream());
                publishState(State.written(outFile.getName()));
                logger.info("File written");
            })
            .doOnError(this::publishErrorAndCancel)
            .doFinally(s -> client.close()).then();
    }

    /**
     * Converts each {@link Locus} from the {@link List} to a {@link VcfObject} by downloading the
     * VCF corresponding to these {@link Locus}
     */
    private Flux<VcfObject> getVcf(List<Locus> locus) {
        return Flux.fromIterable(locus).flatMap(
            l -> getReader(l.getChromosome()).map(ThrowingFunction.unchecked(reader -> {
                publishState(State.downloadingLines(l));
                logger.info(String.format("Downloading lines for locus %s", l));
                var lines = reader.query(l.getChromosome(), l.getStart(), l.getEnd());
                return new VcfObject((VCFHeader) reader.getHeader(), lines);
            })).doOnError(e -> publishErrorAndCancel(new VcfStreamingException(e)))
        );
    }

    /**
     * Creates a VCF reader for the given chromosome (downloads the VCF header)
     */
    private Mono<FeatureReader<VariantContext>> getReader(String chromosome) {
        // TODO: each getReader call could get into an IOException Error

        return Mono.defer(() -> client.getReader(chromosome))
            .doOnSubscribe(s -> {
                publishState(State.downloadingHeader(chromosome));
                logger.info(String.format("Getting header of chr %s", chromosome));
            })
            .retryWhen(Retry.backoff(NB_RETRY, RETRY_DELAY).filter(IOException.class::isInstance))
            .doOnError(e -> publishErrorAndCancel(e.getCause()));
    }

    /**
     * Merges all {@link VcfObject VcfObjects} from the {@link List} in one {@link VcfObject}
     */
    private Mono<VcfObject> merge(List<VcfObject> vcfObjects) {

        if(vcfObjects.isEmpty())
            return Mono.empty();
        if(vcfObjects.size() == 1)
            return Mono.just(vcfObjects.get(0));

        return Mono.fromCallable(() -> {

            var headers = vcfObjects.stream().map(VcfObject::getHeader).toList();

            Comparator<VariantContext> variantContextComparator;
            try {
                variantContextComparator = headers.get(0).getVCFRecordComparator();
                // TODO: should be compatible (isCompatible) with all others
            } catch (Exception e) {
                variantContextComparator = Comparator.comparingInt(VariantContext::getStart);
                logger.info("Using variant start position as comparator for merging");
            }

            var iteratorCollection = vcfObjects.stream().map(VcfObject::getVariants).toList();

            var sampleList = headers.get(0).getSampleNamesInOrder();
            // TODO: all headers must have same sample entries ??
            // TODO: is the order the same that in VariantContexts ?

            var header = new VCFHeader(VCFUtils.smartMergeHeaders(headers, false), sampleList);
            final var mergingIterator =
                new MergingIterator<>(variantContextComparator, iteratorCollection);

            return new VcfObject(header, mergingIterator);
        }).doOnError(this::publishErrorAndCancel);
        // TODO: on error, write each VCF separately ?
    }


    /**
     * Set the sample filter for this {@link VcfExport export}
     *
     * @param selection the selected populations
     * @return this {@link VcfExport}
     */
    public VcfExport setFilter(SampleSelection selection) {
        // If the selection.isAllSelected() is true, there is no need to filter the vcf
        if(!selection.isAllSelected()) {
            samples = selection.getSample();
        }
        return this;
    }


}

