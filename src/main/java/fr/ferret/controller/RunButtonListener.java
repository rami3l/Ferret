package fr.ferret.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;

/**
 * Listens events of the run button and sends input data to the model
 */
public class RunButtonListener implements ActionListener {
    /**
     * The ferret frame
     */
    private final FerretFrame frame;

    /**
     * @param frame The ferret frame
     * @param runButton The button to listen
     */
    public RunButtonListener(FerretFrame frame, JButton runButton) {
        this.frame = frame;
        runButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser saveFileChooser = new JFileChooser();
        String fileNameAndPath;
        saveFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        saveFileChooser.setDialogTitle(Resource.getTextElement("run.save"));
        int returnVal = saveFileChooser.showSaveDialog(frame.getRunPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = saveFileChooser.getSelectedFile();
            fileNameAndPath = file.getAbsolutePath();
            validateInfosAndRun(fileNameAndPath);
        }
    }

    private void validateInfosAndRun(String fileNameAndPath) {
        var controller = switch (getFrame().getInputTabs().getSelectedIndex()) {
            case 1 -> new GenePanelController(frame, frame.getGenePanel());
            case 2 -> new VariantPanelController(frame, frame.getVariantPanel());
            default -> new LocusPanelController(frame, frame.getLocusPanel());
        };
        controller.validateInfosAndRun(fileNameAndPath);
    }

    public FerretFrame getFrame() {
        return frame;
    }
}
