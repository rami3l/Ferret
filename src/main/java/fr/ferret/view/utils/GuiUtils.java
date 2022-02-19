package fr.ferret.view.utils;

import fr.ferret.utils.Resource;
import lombok.experimental.UtilityClass;

import java.awt.GridBagConstraints;
import java.io.File;
import java.util.Optional;
import javax.swing.*;

@UtilityClass
public class GuiUtils {

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


    public Optional<File> chooseFile(JPanel panel, int mode) {
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.setFileSelectionMode(mode);
        saveFileChooser.setDialogTitle(Resource.getTextElement("run.save"));
        int returnVal = saveFileChooser.showSaveDialog(panel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return Optional.of(saveFileChooser.getSelectedFile());
        }
        return Optional.empty();
    }
}
