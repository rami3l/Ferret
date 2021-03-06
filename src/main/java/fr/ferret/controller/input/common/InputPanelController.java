package fr.ferret.controller.input.common;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.model.Region;
import fr.ferret.model.SampleSelection;
import fr.ferret.model.locus.Locus;
import fr.ferret.model.state.State;
import fr.ferret.model.vcf.VcfExport;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.StatePanel;
import fr.ferret.view.utils.GuiUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The base for input panel controllers (locus, gene and variant panel controllers)
 */
@AllArgsConstructor
public abstract class InputPanelController {

    private static final Logger logger = Logger.getLogger(InputPanelController.class.getName());

    /** The main ferret frame */
    @Getter
    protected final FerretFrame frame;

    /**
     * Validates input and runs the program if it's valid
     */
    public abstract void validateInfoAndRun();

    /**
     * Ask the user for a file and runs the given action if file selected
     * @param action The action to execute with the selected file
     */
    protected void run(Consumer<? super File> action) {
        GuiUtils.chooseFile(frame.getBottomPanel().getRunButton(), true, null)
            .ifPresentOrElse(action, this::actionOnFileNotSelected);
    }

    private void actionOnFileNotSelected() {
        frame.getBottomPanel().addState(Resource.getTextElement("run.fileNotSelected"), null).complete();
        logger.info("File not selected...");
    }

    /**
     * Resets the RegionPanel borders and gets all selected populations by zone
     *
     * @return A list of the selected zones (using the zones codes of the {@link Region} class)
     */
    protected SampleSelection getSelectedPopulations() {
        frame.getRegionPanel().resetBorders();
        var selection = new SampleSelection();
        // We add to the selection all the zones associated with a selected checkbox
        if(frame.getRegionPanel().isAllPopulationSelected()) {
            selection.selectAllFor(Resource.config().getSelectedPhase());
        } else {
            frame.getRegionPanel().getRegions().forEach(
                region -> region.getCheckBoxes().entrySet().stream()
                    .filter(entry -> entry.getKey().isSelected())
                    .forEach(entry -> selection.add(entry.getValue()))
            );
        }
        return selection;
    }


    /**
     * Downloads the VCF for the given {@link Locus} list and saves the file for the given selection
     *
     * @param populations The sample selection (people) to include in the VCF file
     * @param outFile The {@link File} to write the VCF to
     * @param locusList The {@link Locus} {@link List} to download the VCF for
     * @param download The {@link StatePanel} to use for displaying the operation progress
     */
    protected void downloadVcf(SampleSelection populations, File outFile, List<Locus> locusList, StatePanel download) {
        logger.log(Level.INFO, "Starting locus download...");
        // Sets the vcf export processus
        var vcfProcessus =
            new VcfExport(locusList, outFile, Resource.config().getSelectedOutputType())
                .setFilter(populations);
        download.setAssociatedProcessus(vcfProcessus);

        // Starts the processus and subscribes its states
        vcfProcessus.start()
            .doOnNext(state -> {
                if(state.getAction() == State.States.CANCELLED)
                    logger.log(Level.INFO, "Download to {0} cancelled", outFile.getName());
            })
            .doOnComplete(download::complete).doOnError(e -> {
                logger.log(Level.WARNING, "Error while downloading or writing", e);
                ExceptionHandler.show(e);
                download.error();
            }).subscribe(download::setState);
    }

}
