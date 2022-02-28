package fr.ferret.view.panel;

import fr.ferret.controller.RunButtonListener;
import fr.ferret.view.FerretFrame;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static fr.ferret.utils.Resource.BUTTON_COLOR;
import static fr.ferret.utils.Resource.getTextElement;

public class BottomPanel extends JPanel {

    /** The panel containing the states of current downloads */
    private final JPanel statesPanel;
    private final LayoutManager layout;
    private final java.util.List<StatePanel> states = new ArrayList<>();

    public BottomPanel(FerretFrame frame) {

        // TODO: update the layout (add contraints ?) to have a better disposition of panels
        layout = new GridBagLayout();
        this.setLayout(layout);

        // Button to launch Ferret action
        var runButton = generateRunButton();
        new RunButtonListener(frame, runButton);

        // the states panel
        statesPanel = generateStatesPanel();

        this.add(statesPanel);
        this.add(runButton);
    }

    private JButton generateRunButton() {
        JButton runButton = new JButton(getTextElement("run.button"));
        runButton.setPreferredSize(new Dimension(300, 60));
        runButton.setBackground(BUTTON_COLOR);
        return runButton;
    }

    private JPanel generateStatesPanel() {
        var states = new JPanel();
        states.setLayout(new GridBagLayout());
        states.setAlignmentX(LEFT_ALIGNMENT);
        // TODO: find a way to add a scrollbar when there are too many downloads at the same time
        //states.setAutoscrolls(true);
        return states;
    }


    public StatePanel addState(String textElement, File downloadLocation) {
        var statePanel = new StatePanel(textElement, downloadLocation);
        statesPanel.add(statePanel);
        return statePanel;
    }

    // TODO: this doesn't seem to work correctly
    public void removeState(StatePanel panel) {
        statesPanel.remove(panel);
    }

}
