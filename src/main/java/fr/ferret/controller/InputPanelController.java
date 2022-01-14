package fr.ferret.controller;

import java.util.ArrayList;
import java.util.List;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.RegionPanel;

/**
 * The base for input panel controllers (locus, gene and variant panel controllers)
 */
public abstract class InputPanelController {
    /**
     * The main ferret frame
     */
    private final FerretFrame frame;

    protected InputPanelController(FerretFrame frame) {
        this.frame = frame;
    }

    public FerretFrame getFrame() {
        return frame;
    }

    /**
     * Validates input and runs the program if it's valid
     * 
     * @param fileNameAndPath The "save to" file path
     */
    public abstract void validateInfosAndRun(String fileNameAndPath);

    /**
     * Resets the RegionPanel borders and gets all selected populations by zone
     *
     * @return A list of the selected zones (using the zones codes of the
     *         {@link fr.ferret.view.region.Region} class)
     */
    protected List<CharSequence> getSelectedPopulations() {
        frame.getRegionPanel().setBorder(null);
        List<CharSequence> populations = new ArrayList<>();
        for (RegionPanel.ZonesPanel regionPanel : frame.getRegionPanel().getRegions()) {
            for (int i = 0; i < regionPanel.getCheckBoxes().length; i++) {
                if (regionPanel.getCheckBoxes()[i].isSelected()) {
                    // Add the selected region to the populations list
                    populations.add(regionPanel.getRegion().getZones()[i]);
                }
            }
        }
        return populations;
    }
}
