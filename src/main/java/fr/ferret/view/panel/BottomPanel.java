package fr.ferret.view.panel;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
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
    private final LayoutManager layout;
    private final java.util.List<StatePanel> states = new ArrayList<>();

    public BottomPanel(FerretFrame frame) {

        // TODO: update the layout (add contraints ?) to have a better disposition of panels
        layout = new GridBagLayout();
        this.setLayout(layout);

        // Button to launch Ferret action
        runButton = generateRunButton();
        new RunButtonListener(frame, runButton);

        // the states panel
        statesPanel = generateStatesPanel();

        this.add(statesPanel);
        this.add(runButton);
    }

    private JButton generateRunButton() {
        JButton runButton = new JButton(Resource.getTextElement("run.button"));
        runButton.setPreferredSize(new Dimension(300, 60));
        runButton.setBackground(Resource.BUTTON_COLOR);
        return runButton;
    }

    private JPanel generateStatesPanel() {
        var states = new JPanel();
        states.setLayout(new GridBagLayout());
        states.setAlignmentX(LEFT_ALIGNMENT);
        // TODO: find a way to add a scrollbar when there are too many downloads at the same time
        // states.setAutoscrolls(true);
        return states;
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
