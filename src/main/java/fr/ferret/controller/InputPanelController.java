package fr.ferret.controller;

import java.awt.*;
import java.util.List;
import fr.ferret.model.Region;
import fr.ferret.model.ZoneSelection;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.*;

/**
 * The base for input panel controllers (locus, gene and variant panel controllers)
 */
@AllArgsConstructor
public abstract class InputPanelController<T extends JPanel> {

    /** The main ferret frame */
    @Getter
    protected final FerretFrame frame;
    /** The panel which is controlled by this {@link InputPanelController} */
    protected final T panel;

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

    /**
     * Use it to create an error message and highlight components
     */
    @NoArgsConstructor
    protected final class Error {

        private final StringBuilder errorMessage =
            new StringBuilder(Resource.getTextElement("run.fixerrors"));

        public Error append(String element, Object... args) {
            errorMessage.append("\n ").append(String.format(Resource.getTextElement(element), args));
            return this;
        }

        public void highlight(JComponent... components) {
            List.of(components).forEach(component -> component
                .setBorder(BorderFactory.createLineBorder(Color.RED, 1)));
        }

        public void show() {
            JOptionPane.showMessageDialog(getFrame(), errorMessage,
                Resource.getTextElement("run.error"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
