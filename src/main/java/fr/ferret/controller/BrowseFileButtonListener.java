package fr.ferret.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.ferret.utils.Resource;
import fr.ferret.view.utils.GuiUtils;
import lombok.Getter;

/**
 * Listens events of the run button and sends input data to the model
 */
@Getter
public class BrowseFileButtonListener implements ActionListener {
    /**
     * The panel containing the button
     */
    private final JPanel panel;
    /**
     * The listened button
     */
    private final JButton runButton;
    /**
     * The status label
     */
    private final JLabel selectedFileLabel;
    /**
     * The file selected by the user
     */
    private File selectedFile;

    /**
     * @param panel The panel owning the button
     * @param runButton The button to listen
     * @param selectedFileLabel The status label
     */
    public BrowseFileButtonListener(JPanel panel, JButton runButton, JLabel selectedFileLabel) {
        this.panel = panel;
        this.runButton = runButton;
        this.selectedFileLabel = selectedFileLabel;
        runButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var file = GuiUtils.chooseFile(panel, JFileChooser.FILES_ONLY);
        file.ifPresent(f -> {
            selectedFile = f;
            selectedFileLabel.setText(Resource.getTextElement("browse.selectedfile")
                + f.getAbsolutePath());
        });
    }
}
