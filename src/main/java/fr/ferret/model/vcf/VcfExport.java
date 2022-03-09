package fr.ferret.model.vcf;

import com.pivovarit.function.ThrowingFunction;
import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.model.State;
import fr.ferret.model.ZoneSelection;
import fr.ferret.model.locus.Locus;
import fr.ferret.model.utils.FileWriter;
import fr.ferret.utils.Resource;
import htsjdk.samtools.util.MergingIterator;
import htsjdk.tribble.FeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * An object representing a VCF export. It is constructed with a {@link Locus} {@link Flux}. We can
 * set a {@link ZoneSelection population filter} with the <i>setFilter</i> method. We start the
 * export with the <i>start</i> method, passing it the {@link File outFile}
 */
public class VcfExport {

    private static final Logger logger = Logger.getLogger(VcfExport.class.getName());

    private final Flux<Locus> locusFlux;
    private FluxSink<State> state;
    private Set<String> samples;

    /**
     * The phase to use for getting variants (default: selected version)
     */
    private final Phases1KG phase1KG = Resource.CONFIG.getSelectedVersion();

    /**
     * Constructs a {@link VcfExport}.
     *
     * @param locusFlux A flux containing all the locus to export in a VCF file.
     */
    public VcfExport(Flux<Locus> locusFlux) {
        this.locusFlux = locusFlux;
    }

    //TODO: do also this method for 1 Locus only
    private Flux<VcfObject> getVcf(Flux<Locus> locus) {
        // TODO: what if start > end (locus) ?
        return locus.flatMap(
            l -> getReader(l.getChromosome()).map(ThrowingFunction.unchecked(reader -> {
                state.next(new State(State.DOWNLOADING_LINES, l.getChromosome(), l));
                logger.info(String.format("Downloading lines for locus %s", l));
                var lines = reader.query(l.getChromosome(), l.getStart(), l.getEnd());
                return new VcfObject((VCFHeader) reader.getHeader(), lines);
            })).doOnError(ExceptionHandler::vcfStreamingError)
            // TODO: on error ?
        );
    }

    private Mono<FeatureReader<VariantContext>> getReader(String chromosome) {
        // TODO: each getReader call could get into an IOException Error
        return new IgsrClient(
            Resource.getVcfUrlTemplate(phase1KG)).getReader(chromosome)
            .doOnSubscribe(s -> {
                state.next(new State(State.DOWNLOADING_HEADER, chromosome, chromosome));
                logger.info(String.format("Downloading header of chr %s", chromosome));
            })
            .doOnError(e -> {
                ExceptionHandler.connectionError(e);
                // TODO: error if one reader failed ? retry ?
                state.error(e);
            });
    }

    /**
     * Merges all {@link VcfObject VcfObjects} from the {@link List} in one {@link VcfObject}
     */
    private VcfObject merge(List<VcfObject> vcfObjects) {

        var headers = vcfObjects.stream().map(VcfObject::getHeader).toList();

        var variantContextComparator = headers.get(0).getVCFRecordComparator();
        // TODO: should be compatible (isCompatible) with all others

        var iteratorCollection = vcfObjects.stream().map(VcfObject::getVariants).toList();

        var sampleList = headers.get(0).getSampleNamesInOrder();
        // TODO: all headers must have same sample entries ??
        // TODO: is the order the same that in VariantContexts ?

        // TODO: catch IllegalStateException
        var header = new VCFHeader(VCFUtils.smartMergeHeaders(headers, false), sampleList);
        final var mergingIterator =
            new MergingIterator<>(variantContextComparator, iteratorCollection);

        return new VcfObject(header, mergingIterator);
    }

    /**
     * Exports a "distilled" VCF file from an IGSR online database query.
     *
     * @param outFile the output {@link File}
     * TODO: @param samples the sample names, e.g. {HG00096, HG0009}
     * @return {@link Flux} of {@link String strings} indicating the progress of the treatment
     */
    public Flux<State> start(File outFile) {
        return Flux.create(s -> {
            state = s;
            getVcf(locusFlux).subscribeOn(Schedulers.boundedElastic()).collect(Collectors.toList())
                .map(this::merge).doOnNext(vcf -> {
                    if (samples != null) {
                        logger.info("Filtering by samples");
                        vcf = vcf.filter(samples);
                    }
                    state.next(new State(State.WRITING, outFile.getName(), null));
                    logger.info("Writing to disk...");
                    FileWriter.writeVCF(outFile, vcf.getHeader(), vcf.getVariants().stream());
                    state.next(new State(State.WRITTEN, outFile.getName(), null));
                    logger.info("File written");
                }).doOnError(FileSystemException.class, ExceptionHandler::fileWritingError)
                .doOnError(ExceptionHandler::unknownError).subscribe();
            // TODO: errors
        });
    }

    /**
     * Set the sample filter for this {@link VcfExport export}
     *
     * @param selection the selected populations
     * @return this {@link VcfExport}
     */
    public VcfExport setFilter(ZoneSelection selection) {
        try {
            samples = Resource.getSamples(phase1KG, selection);
        } catch (IOException e) {
            ExceptionHandler.ressourceAccessError(e);
        }
        return this;
    }


}

