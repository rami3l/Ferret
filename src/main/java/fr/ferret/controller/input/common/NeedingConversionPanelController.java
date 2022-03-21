package fr.ferret.controller.input.common;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.exceptions.ConversionIncompleteException;
import fr.ferret.model.SampleSelection;
import fr.ferret.model.locus.Locus;
import fr.ferret.model.state.PublishingStateProcessus;
import fr.ferret.model.state.State;
import fr.ferret.view.FerretFrame;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class NeedingConversionPanelController extends InputPanelController {

    private static final Logger logger = Logger.getLogger(NeedingConversionPanelController.class.getName());

    protected NeedingConversionPanelController(FerretFrame frame) {
        super(frame);
    }

    protected abstract boolean confirmContinue(String notFound);
    protected abstract PublishingStateProcessus<List<Locus>> getConversionProcessus(List<String> toConvert);

    protected void convertAndDownloadVcf(SampleSelection populations, List<String> toConvert, int windowSize) {
        run(outFile -> {

            var download = frame.getBottomPanel().addState("Starting download", outFile);

            // Sets the conversion processus
            var conversion = getConversionProcessus(toConvert);
            download.setAssociatedProcessus(conversion);

            var notFound = new AtomicReference<>("");

            // Starts the processus and subscribes to its state
            conversion.start().doOnComplete(() -> {
                if ("".equals(notFound.get()) || confirmContinue(notFound.get())) {
                    var locusList = conversion.getResult();
                    if (windowSize != 0)
                        locusList = locusList.stream().map(l -> l.withWindow(windowSize)).toList();
                    downloadVcf(populations, outFile, locusList, download);
                } else {
                    download.cancel();
                    logger.log(Level.INFO, "Download to {0} cancelled", outFile.getName());
                }
            }).doOnError(e -> {
                logger.log(Level.WARNING, "Error while downloading or writing", e);
                download.error();
                ExceptionHandler.show(e);
            }).doOnNext(state -> {
                if (state.getAction() == State.States.CONFIRM_CONTINUE
                    && state.getObjectBeingProcessed() instanceof ConversionIncompleteException e) {
                    notFound.set(String.join(",", e.getNotFound()));
                } else if (state.getAction() == State.States.CANCELLED) {
                    logger.log(Level.INFO, "Download to {0} cancelled", outFile.getName());
                }
            }).subscribe(download::setState);
        });
    }
}
