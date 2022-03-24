package fr.ferret.view.utils;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.ferret.controller.settings.FileOutputType;
import fr.ferret.utils.Resource;
import fr.ferret.utils.Utils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GuiUtils {

    private final Logger logger = Logger.getLogger(GuiUtils.class.getName());

    /**
     * Adds a component to a panel (which is a grid)
     * 
     * @param panel The panel to add the component to
     * @param componentToAdd The component to add to the panel
     * @param weightx The width of the component in the panel
     * @param gridx The x position of the component in the panel
     * @param gridy The y position of the component in the panel
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

    /**
     * Open a popup to let the user choose a file
     *
     * @param parent The parent {@link Component} of the popup
     * @param save The selection mode (true → save, false → open)
     * @return An {@link Optional} {@link File} (empty if no file selected)
     */
    public Optional<File> chooseFile(Component parent, boolean save, @Nullable FileNameExtensionFilter filter) {
        var fileChooser = save ? new SaveFileChooser() : new OpenFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle(Resource.getTextElement(save ? "run.save" :
            "input.noFileSelected"));
        if(filter!=null) {
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(filter);
        }
        int returnVal = fileChooser.showDialog(parent,
                Resource.getTextElement(save ? "run.saveButtonText" : "gene.openButtonText"));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return Optional.ofNullable(fileChooser.getSelectedFile());
        }
        return Optional.empty();
    }

    /**
     * Opens the location of the given file in the file explorer. Selects the file in the explorer
     * if that option is supported
     *
     * @param file The file to open the location of
     */
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

    /**
     * Browses the given target
     *
     * @param target The URI to browse
     */
    public void browse(URI target) {
        try {
            Desktop.getDesktop().browse(target);
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Impossible to browse %s", target), e);
        }
    }

    /**
     * FileChooser which confirms the will to overwrite the selected file if it already exists
     */
    private static class SaveFileChooser extends JFileChooser {
        @Override
        public void approveSelection() {
            if (getSelectedFile() == null)
                return;
            var filename = removeExtensionIfKnown(getSelectedFile().getPath());
            var outputType = Resource.config().getSelectedOutputType();
            var existingFiles = outputType.extensions().stream()
                .map(ext -> new File(filename + "." + ext))
                .filter(File::exists)
                .map(File::getName)
                .toList();
            if (!existingFiles.isEmpty()) {
                // If the file exists, we open a confirmation dialog
                int result = JOptionPane.showConfirmDialog(this,
                        Resource.getTextElement("run.fileExistingMsg", String.join(", ", existingFiles)),
                        Resource.getTextElement("run.fileExistingTitle"),
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    super.approveSelection();
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    cancelSelection();
                }
                return;
            }
            super.approveSelection();
        }

        private String removeExtensionIfKnown(String filename) {
            var knownExtensions = Arrays.stream(FileOutputType.Extension.values())
                .map(FileOutputType.Extension::toString).toList();
            return Utils.removeExtensionIfInList(filename, knownExtensions);
        }
    }


    /**
     * File chooser which checks if the selected file exists
     */
    private static class OpenFileChooser extends JFileChooser {
        @Override
        public void approveSelection() {
            if (getSelectedFile() != null && getSelectedFile().isFile())
                super.approveSelection();
        }
    }
}
