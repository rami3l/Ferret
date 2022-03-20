package fr.ferret.view.panel;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import fr.ferret.model.Region;
import fr.ferret.model.Zone;
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
    private ZonesPanel allPopulations;

    public boolean isAllPopulationSelected(){
        return allPopulations.getCheckBoxes().keySet().stream().findFirst()
            .map(JCheckBox::isSelected).orElse(false);
    }

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
     * Reloads the contents of the panel. <br> Called when the Ferret settings are modified
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
        // TODO: adapt this to work with any number of regions
        JPanel container = new JPanel(new GridLayout(2, 3));

        var phaseRegions = Resource.getSample(Resource.config().getSelectedPhase());

        var allPopulationRegion = new Region("ALL", phaseRegions.stream().mapToInt(Region::getNbPeople).sum());
        allPopulations = new ZonesPanel(allPopulationRegion, 9, true);
        container.add(RegionPanel.this.allPopulations);

        phaseRegions.forEach(region -> {
            // TODO: adapt the number of lines → we should use the maximum number of zones for the regions of the current row
            ZonesPanel panel = new ZonesPanel(region, 9, false);
            regions.add(panel);
            container.add(panel);
        });

        add(container, BorderLayout.CENTER);
    }



    /**
     * A JPanel containing a {@link Region} <br> Contains all the selectable zones of the region
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
        private final Map<JCheckBox, Zone> checkBoxes = new LinkedHashMap<>();

        /**
         * Creates a panel for the given region
         * 
         * @param region The region
         * @param lines The number of lines of the layout, to keep coherence with other displayed
         *        SubPanels
         * @param isGlobal Boolean indicating that this zone panel is the "All population" one<br>
         * TODO: should we use "ALL".equals(region.getAbbrev()) instead of isGlobal ?
         */
        public ZonesPanel(Region region, int lines,  boolean isGlobal) {

            this.region = region;
            this.setLayout(new GridLayout(lines, 1));

            // Zone panel title
            JLabel label = new JLabel(Resource.getTextElement("region." + region.getAbbrev()));
            label.setFont(Resource.ZONE_LABEL_FONT);
            label.setForeground(Resource.ZONE_LABEL_COLOR);
            add(label);

            var regionCheckbox = addCheckbox(region, true);
            region.getZones().forEach(zone -> addCheckbox(zone, false));

            // When we select the first checkbox (the region checkbox) others are disabled.
            regionCheckbox.addActionListener(action -> {
                boolean state = !regionCheckbox.isSelected();
                setCheckBoxesState(state, false);

                // If we selected the "All population" checkbox, all others checkboxes are disabled
                if (isGlobal) {
                    for (ZonesPanel panel : RegionPanel.this.regions) {
                        if (panel != this) {
                            panel.setCheckBoxesState(state, true);
                        }
                    }
                }
            });
        }

        /**
         * Creates a checkbox for the given {@link Zone} (or {@link Region}) and makes its label
         * bold according to the bold boolean argument
         */
        private JCheckBox addCheckbox(Zone zone, boolean bold) {

            // creates the checkbox and setting his font
            var checkbox = new JCheckBox(
                zone.getAbbrev() + " " + zone.getName() + " (n=" + zone.getNbPeople() + ")");
            checkbox.setFont(new Font(checkbox.getFont().getFontName(), bold ? Font.BOLD : Font.PLAIN, 14));

            // adds the checkbox to the checkBoxes map and the panel, then returns it
            checkBoxes.put(checkbox, zone);
            add(checkbox);
            return checkbox;
        }

        /**
         * Changes the state of the checkboxes in this panel
         *
         * @param state The new selected state of the checkboxes
         * @param regionsAlso Boolean indicating if the state change must also be applied to the regions
         */
        private void setCheckBoxesState(boolean state, boolean regionsAlso) {
            checkBoxes.forEach((checkBox, zone) -> {
                if (regionsAlso || !(zone instanceof Region)) {
                    checkBox.setEnabled(state);
                    checkBox.setSelected(false);
                    checkBox.updateUI();
                }
            });
        }
    }
}
