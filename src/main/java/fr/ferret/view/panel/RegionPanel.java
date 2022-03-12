package fr.ferret.view.panel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import fr.ferret.model.Region;
import fr.ferret.utils.Resource;
import lombok.Getter;

/**
 * Region panel <br>
 * Selection of the regions of the 1KG project
 */
public class RegionPanel extends JPanel {

    /**
     * Panels for each supported {@link Region}
     */
    @Getter
    private final List<ZonesPanel> regions = new ArrayList<>();

    /**
     * Inits a new RegionPanel
     */
    public RegionPanel() {

        // Panel settings
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        initPanel();
    }

    /**
     * Reloads the contents of the panel <br>
     * Called when the Ferret settings are modified
     */
    public void reloadPanel() {
        removeAll();
        initPanel();
        updateUI();
    }

    /**
     * Inits the panel : adds all components
     */
    private void initPanel() {
        // Panel title
        JLabel label = new JLabel(Resource.getTextElement("region.input"), SwingConstants.LEFT);
        label.setFont(Resource.TITLE_FONT);
        label.setForeground(Resource.TITLE_COLOR);
        add(label, BorderLayout.NORTH);

        // Container for zone selection panels
        JPanel container = new JPanel(new GridLayout(2, 3));

        Region[] ferretRegions = Resource.config().getSelectedVersion().getRegions();
        for (int i = 0; i < ferretRegions.length; i++) {
            ZonesPanel panel = new ZonesPanel(ferretRegions[i], i >= 3 ? 7 : 9);
            regions.add(panel);
            container.add(panel);
        }

        add(container, BorderLayout.CENTER);
    }



    /**
     * A JPanel containing a {@link Region} <br>
     * Contains all the selectable zones of the region
     */
    @Getter
    public class ZonesPanel extends JPanel {
        /**
         * The region displayed on this panel
         */
        private final transient Region region;
        /**
         * The checkboxes for each zone of the region
         */
        private final JCheckBox[] checkBoxes;

        /**
         * Creates a panel for the given region
         * 
         * @param region The region
         * @param lines The number of lines of the layout, to keep coherence with other displayed
         *        SubPanels
         */
        public ZonesPanel(Region region, int lines) {

            this.region = region;
            this.setLayout(new GridLayout(lines, 1));

            // Zone panel title
            JLabel label = new JLabel(
                    Resource.getTextElement("region." + region.getName().toLowerCase(Locale.ROOT)));
            label.setFont(Resource.ZONE_LABEL_FONT);
            label.setForeground(Resource.ZONE_LABEL_COLOR);
            add(label);

            // Zones selection
            this.checkBoxes = new JCheckBox[region.getZones().length];

            for (int i = 0; i < checkBoxes.length; i++) {

                int nbIndividuals = region.getIndividualCount()[i];

                // Checkbox created with its label
                checkBoxes[i] = new JCheckBox(region.getZones()[i] + " "
                        + Resource.getTextElement("region." + region.getZones()[i]) + " (n="
                        + nbIndividuals + ")");

                // Checkbox font (bold for the first checkbox)
                checkBoxes[i].setFont(new Font(checkBoxes[i].getFont().getFontName(),
                        i == 0 ? Font.BOLD : Font.PLAIN, 14));

                // Checkbox is added to the panel
                add(checkBoxes[i]);

                // If there is no individual for this zone, the checkbox is disabled.
                if (nbIndividuals == 0) {
                    checkBoxes[i].setEnabled(false);
                }
            }

            // When we select the first checkbox (the All zone population one) others are disabled.
            checkBoxes[0].addActionListener(action -> {
                boolean state = !checkBoxes[0].isSelected();
                setCheckBoxesState(1, state);

                // If we selected the All population checkboxes, all others checkboxes are disabled
                if (region == Resource.config().getSelectedVersion().getRegions()[0]) {
                    for (ZonesPanel panel : RegionPanel.this.regions) {
                        if (panel != this) {
                            panel.setCheckBoxesState(0, state);
                        }
                    }
                }
            });
        }

        /**
         * Changes the states of all zone checkboxes between start and checkBoxes.length
         * 
         * @param start The start offset
         * @param state The new selected state of the checkboxes
         */
        private void setCheckBoxesState(int start, boolean state) {
            for (int i = start; i < checkBoxes.length; i++) {
                // We change the state of the checkbox only if there is individuals for the
                // corresponding zone (else we leave it disabled)
                if (region.getIndividualCount()[i] != 0) {
                    checkBoxes[i].setEnabled(state);
                }
                checkBoxes[i].setSelected(false);
                checkBoxes[i].updateUI();
            }
        }
    }
}
