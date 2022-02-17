package fr.ferret.controller;

import java.util.List;
import fr.ferret.model.Region;
import fr.ferret.model.ZoneSelection;
import fr.ferret.view.FerretFrame;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The base for input panel controllers (locus, gene and variant panel controllers)
 */
@AllArgsConstructor
public abstract class InputPanelController {
    /**
     * The main ferret frame
     */
    @Getter
    private final FerretFrame frame;

    /**
     * Validates input and runs the program if it's valid
     * 
     * @param fileNameAndPath The "save to" file path
     */
    public abstract void validateInfosAndRun(String fileNameAndPath);

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
