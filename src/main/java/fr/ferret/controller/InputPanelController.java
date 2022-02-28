package fr.ferret.controller;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import fr.ferret.model.Region;
import fr.ferret.model.ZoneSelection;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.utils.GuiUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;

/**
 * The base for input panel controllers (locus, gene and variant panel controllers)
 */
@AllArgsConstructor
public abstract class InputPanelController<T extends JPanel> {

    private static final Logger logger = Logger.getLogger(InputPanelController.class.getName());

    /** The main ferret frame */
    @Getter
    protected final FerretFrame frame;
    /** The panel which is controlled by this {@link InputPanelController} */
    protected final T panel;

    /**
     * Validates input and runs the program if it's valid
     */
    public abstract void validateInfoAndRun();

    /**
     * Ask the user for a file and runs the given action if file selected
     * @param action The action to execute with the selected file
     */
    protected void run(Consumer<? super File> action) {
        GuiUtils.chooseFile(frame.getBottomPanel(), true)
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
    protected ZoneSelection getSelectedPopulations() {
        frame.getRegionPanel().setBorder(null);
        var selection = new ZoneSelection();
        frame.getRegionPanel().getRegions().forEach(region -> {
            for (int i = 0; i < region.getCheckBoxes().length; i++) {
                if (region.getCheckBoxes()[i].isSelected()) {
                    // Adds the selected region to the populations list
                    if (i == 0) {
                        selection.add(region.getRegion().getAbbrev());
                    } else {
                        String zone = region.getRegion().getZones()[i];
                        selection.add(region.getRegion().getAbbrev(), List.of(zone));
                    }
                }
            }
        });
        return selection;
    }

}
