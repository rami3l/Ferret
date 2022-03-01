package fr.ferret.view.utils;

import fr.ferret.utils.Resource;
import lombok.experimental.UtilityClass;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

@UtilityClass
public class GuiUtils {

    private final Logger logger = Logger.getLogger(GuiUtils.class.getName());

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


    public Optional<File> chooseFile(JPanel panel, boolean save) {
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveFileChooser
                .setDialogTitle(Resource.getTextElement(save ? "run.save" : "gene.selectfile"));
        UIManager.put("FileChooser.saveButtonText",
                Resource.getTextElement(save ? "run.saveButtonText" : "gene.openButtonText"));
        int returnVal = saveFileChooser.showSaveDialog(panel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return Optional.ofNullable(saveFileChooser.getSelectedFile());
        }
        return Optional.empty();
    }

    public void openFileLocation(File file) {
        if (Desktop.isDesktopSupported()) {
            var desktop = Desktop.getDesktop();
            try {
                if (desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
                    desktop.browseFileDirectory(file);
                } else if (desktop.isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(file.getParentFile());
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to open file location", e);
            }
        } else {
            logger.log(Level.WARNING, "Failed to open file location,"
                    + " because desktop actions are not supported on this platform");
        }
    }

    public void browse(URI target) {
        try {
            Desktop.getDesktop().browse(target);
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Impossible to browse %s", target), e);
        }
    }
}
