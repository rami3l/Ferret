package fr.ferret.view.utils;

import java.awt.GridBagConstraints;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class GuiUtils {

    // utils classes should not be instanciated
    private GuiUtils() {}

    /**
     * Adds a component to a panel (which is a grid)
     * 
     * @param panel : the panel to add the component to
     * @param componentToAdd : the component to add to the panel
     * @param weightx : the width of the component in the panel
     * @param gridx : the x position of the component in the panel
     * @param gridy : the y position of the component in the panel
     */
    public static void addToPanel(JPanel panel, JComponent componentToAdd, double weightx,
            int gridx, int gridy) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = weightx;
        c.gridx = gridx;
        c.gridy = gridy;
        panel.add(componentToAdd, c);
    }

}
