package fr.ferret.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import fr.ferret.controller.input.GenePanelController;
import fr.ferret.controller.input.LocusPanelController;
import fr.ferret.controller.input.VariantPanelController;
import fr.ferret.view.FerretFrame;

/**
 * Listens events of the run button and sends input data to the model
 */
public class RunButtonListener implements ActionListener {

    /** The ferret frame */
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
        var controller = switch (frame.getInputTabs().getSelectedIndex()) {
            case 1 -> new GenePanelController(frame);
            case 2 -> new VariantPanelController(frame);
            default -> new LocusPanelController(frame);
        };
        controller.validateInfoAndRun();
    }
}
