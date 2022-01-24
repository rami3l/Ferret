package fr.ferret.view.panel;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JPanel;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;

/**
 * The panel with the run button of Ferret
 */
public class RunPanel extends JPanel {

    public RunPanel(FerretFrame frame) {
        // Button to launch Ferret action
        JButton runButton = new JButton(Resource.getTextElement("run.button"));
        runButton.setPreferredSize(new Dimension(300, 60));
        runButton.setBackground(new Color(201, 157, 240));
        this.add(runButton);
    }
}
