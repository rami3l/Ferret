package fr.ferret.view.panel;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import fr.ferret.controller.RunButtonListener;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import lombok.Getter;


public class BottomPanel extends JPanel {

    @Getter
    private final JButton runButton;

    /** The panel containing the states of current downloads */
    private final JPanel statesPanel;
    private final java.util.List<StatePanel> states = new ArrayList<>();

    public BottomPanel(FerretFrame frame) {

        // TODO: update the layout (add contraints ?) to have a better disposition of panels
        this.setLayout(new GridBagLayout());

        // Button to launch Ferret action
        runButton = generateRunButton();
        new RunButtonListener(frame, runButton);

        // the states panel
        statesPanel = generateStatesPanel();

        this.add(statesPanel);
        this.add(runButton);
    }

    private JButton generateRunButton() {
        JButton button = new JButton(Resource.getTextElement("run.button"));
        button.setPreferredSize(new Dimension(300, 60));
        button.setBackground(Resource.BUTTON_COLOR);
        return button;
    }

    private JPanel generateStatesPanel() {
        var panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setAlignmentX(LEFT_ALIGNMENT);
        // TODO: find a way to add a scrollbar when there are too many downloads at the same time
        // states.setAutoscrolls(true);
        return panel;
    }


    public StatePanel addState(String text, File downloadLocation) {
        var statePanel = new StatePanel(text, downloadLocation);
        statesPanel.add(statePanel);
        return statePanel;
    }

    // TODO: this doesn't seem to work correctly
    public void removeState(StatePanel panel) {
        statesPanel.remove(panel);
    }
}
