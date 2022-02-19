package fr.ferret.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import fr.ferret.view.FerretFrame;
import fr.ferret.view.utils.GuiUtils;

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
        var file = GuiUtils.chooseFile(frame.getRunPanel(), JFileChooser.DIRECTORIES_ONLY);
        file.ifPresent(f -> validateInfosAndRun(f.getAbsolutePath()));
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
