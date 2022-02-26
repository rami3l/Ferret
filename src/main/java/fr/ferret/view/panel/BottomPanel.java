package fr.ferret.view.panel;

import fr.ferret.controller.RunButtonListener;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;

import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JPanel {

    /** The panel containing the states of current downloads */
    private final JPanel statesPanel;
    private final LayoutManager layout;

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
        //states.setAutoscrolls(true);
        return states;
    }


    public StatePanel addState(String textElement) {
        var statePanel = new StatePanel(textElement);
        statesPanel.add(statePanel);
        return statePanel;
    }

    public void remove(StatePanel panel) {
        statesPanel.remove(panel);
    }


    public static class StatePanel extends JPanel {

        /** the label describing the current state (downloading header, lines, etc.) */
        private final JLabel stateLabel;
        /** The button to open the download location */
        private final JButton openButton;

        public StatePanel(String textElement) {

            stateLabel = new JLabel(textElement);

            // TODO: Add a spinner visible until the download completes
            openButton = new JButton();
            openButton.setVisible(false);
            openButton.setSize(10, 10);
            openButton.setToolTipText("Open download location");
            Resource.getIcon("/img/open-folder.png")
                .ifPresentOrElse(openButton::setIcon, () -> openButton.setText("Open"));

            this.add(stateLabel);
            this.add(openButton);
        }

        public void setState(String textElement) {
            stateLabel.setText(textElement);
        }

        public void complete() {
            // This button is only visible when the download is complete
            openButton.setVisible(true);
            // TODO: add open download location implementation
        }
    }
}
