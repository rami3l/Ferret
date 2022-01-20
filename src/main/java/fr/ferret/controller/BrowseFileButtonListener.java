package fr.ferret.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import fr.ferret.utils.Resource;
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
        JFileChooser saveFileChooser = new JFileChooser();
        String fileNameAndPath;
        saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveFileChooser.setDialogTitle(Resource.getTextElement("run.save"));
        int returnVal = saveFileChooser.showSaveDialog(panel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = saveFileChooser.getSelectedFile();
            fileNameAndPath = file.getAbsolutePath();
            selectedFileLabel.setText(
                    Resource.getTextElement("browse.selectedfile") + " " + fileNameAndPath);
            selectedFile = file;
        }
    }
}
