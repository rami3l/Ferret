package fr.ferret.view.panel;

import fr.ferret.controller.RunButtonListener;
import fr.ferret.view.FerretFrame;

import javax.swing.*;
import java.awt.*;

import static fr.ferret.utils.Resource.*;

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


    public StatePanel addState(String textElement) {
        var statePanel = new StatePanel(textElement);
        statesPanel.add(statePanel);
        return statePanel;
    }

    // TODO: when the mouse has not hovered the state panel for a certain time, hide it (progressively)
    public void remove(StatePanel panel) {
        statesPanel.remove(panel);
    }


    public static class StatePanel extends JPanel {

        /** the label describing the current state (downloading header, lines, etc.) */
        private final JLabel stateLabel;
        /** The spinner displayed while downloading */
        private final JLabel spinner;
        /** The button to open the download location */
        private final JButton openButton;

        public StatePanel(String text) {

            stateLabel = new JLabel(text);
            spinner = new JLabel();
            getIcon("/img/loading.gif")
                .ifPresentOrElse(spinner::setIcon, () -> spinner.setText("..."));
            spinner.setToolTipText(getTextElement("tooltip.downloading"));

            openButton = new JButton();
            openButton.setVisible(false);
            openButton.setSize(10, 10);

            this.add(stateLabel);
            this.add(spinner);
            this.add(openButton);
        }

        public void setState(String text) {
            stateLabel.setText(text);
        }

        public void complete() {
            // When the download is complete, hides spinner and makes the open button visible
            spinner.setVisible(false);
            openButton.setToolTipText(getTextElement("tooltip.openDownload"));
            getIcon("/img/open-folder.png")
                .ifPresentOrElse(openButton::setIcon, () -> openButton.setText(getTextElement("button.open")));
            openButton.setVisible(true);
            // TODO: add open download location implementation
        }
    }
}
