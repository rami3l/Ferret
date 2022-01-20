package fr.ferret.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
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
import fr.ferret.utils.Resource;
import fr.ferret.view.region.Region;
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
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        initPanel();
    }

    /**
     * Inits the panel : adds all components
     */
    private void initPanel() {
        JLabel label = new JLabel(Resource.getTextElement("region.input"), SwingConstants.LEFT);
        label.setFont(new Font("Calibri", Font.BOLD, 24));
        add(label, BorderLayout.NORTH);
        label.setForeground(new Color(18, 0, 127));

        JPanel container = new JPanel();
        container.setLayout(new GridLayout(2, 3));

        Region[] ferretRegions = Resource.CONFIG.getSelectedVersion().getRegions();
        for (int i = 0; i < ferretRegions.length; i++) {
            Region region = ferretRegions[i];
            ZonesPanel panel = new ZonesPanel(region, i >= 3 ? 7 : 9);
            regions.add(panel);
            container.add(panel);
        }

        add(container, BorderLayout.CENTER);
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
     * A JPanel containing a {@link Region} <br>
     * Contains all the selectable zones of the region
     */
    @Getter
    public class ZonesPanel extends JPanel {
        /**
         * The region displayed on this panel
         */
        private final Region region;
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

            // Title
            JLabel label = new JLabel(
                    Resource.getTextElement("region." + region.getName().toLowerCase(Locale.ROOT)));
            label.setFont(new Font("Calibri", Font.BOLD, 20));
            label.setForeground(new Color(131, 55, 192));
            add(label);

            // Zones selection
            this.checkBoxes = new JCheckBox[region.getZones().length];
            for (int i = 0; i < checkBoxes.length; i++) {
                checkBoxes[i] = new JCheckBox(region.getZones()[i] + " "
                        + Resource.getTextElement("region." + region.getZones()[i]) + " (n="
                        + region.getIndividualCount()[i] + ")");
                checkBoxes[i].setFont(new Font(checkBoxes[i].getFont().getFontName(),
                        i == 0 ? Font.BOLD : Font.PLAIN, 14));
                add(checkBoxes[i]);
                if (region.getIndividualCount()[i] == 0) {
                    checkBoxes[i].setEnabled(false);
                }
            }
            checkBoxes[0].addActionListener(action -> {
                boolean state = !checkBoxes[0].isSelected();
                setCheckBoxesState(1, state);

                if (region == Resource.CONFIG.getSelectedVersion().getRegions()[0]) {
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
                if (region.getIndividualCount()[i] != 0) {
                    checkBoxes[i].setEnabled(state);
                }
                checkBoxes[i].setSelected(false);
                checkBoxes[i].updateUI();
            }
        }
    }
}
