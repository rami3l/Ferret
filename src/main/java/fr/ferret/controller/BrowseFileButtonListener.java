package fr.ferret.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.ferret.utils.Resource;
import fr.ferret.view.utils.GuiUtils;
import lombok.Getter;

/**
 * Listens events of the run button and sends input data to the model
 */
@Getter
public class BrowseFileButtonListener implements ActionListener {

    private static final int FILE_DISPLAY_MAX_LENGTH = 22;
    private final FileNameExtensionFilter extensionFilter;
    /**
     * The panel containing the button
     */
    private final JPanel panel;
    /**
     * The listened button
     */
    private final JButton browseButton;
    /**
     * The status label
     */
    private final JLabel selectedFileLabel;
    private final String selectedFileDefaultText;
    /**
     * The file selected by the user
     */
    private File selectedFile;


    /**
     * @param panel The panel owning the button
     * @param browseButton The button to listen
     * @param selectedFileLabel The status label
     */
    public BrowseFileButtonListener(JPanel panel, JButton browseButton, JLabel selectedFileLabel) {
        this.panel = panel;
        this.browseButton = browseButton;
        this.selectedFileLabel = selectedFileLabel;
        this.selectedFileDefaultText = selectedFileLabel.getText();
        browseButton.addActionListener(this);
        var description = Resource.getTextElement("input.extensionsDescription");
        extensionFilter = new FileNameExtensionFilter(description, Resource.inputExtensions);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var file = GuiUtils.chooseFile(browseButton, false, extensionFilter);
        file.ifPresent(f -> {
            var filename = f.getName();
            if(filename.length()>FILE_DISPLAY_MAX_LENGTH) {
                filename = filename.substring(0, FILE_DISPLAY_MAX_LENGTH - 3) + "...";
            }
            selectedFile = f;
            selectedFileLabel.setText(filename);
            selectedFileLabel.setToolTipText(f.getAbsolutePath());
        });
    }

    public void reset() {
        selectedFile = null;
        selectedFileLabel.setText(selectedFileDefaultText);
    }
}
