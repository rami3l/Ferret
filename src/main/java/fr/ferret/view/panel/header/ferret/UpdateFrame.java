package fr.ferret.view.panel.header.ferret;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import fr.ferret.controller.UpdateChecker;
import fr.ferret.utils.Resource;
import fr.ferret.view.LinkLabel;

public class UpdateFrame extends JFrame {

    private boolean checkedForUpdate = false;
    private final JPanel updatePanel = new JPanel();
    private final JPanel updateBarHolder = new JPanel();
    private final JProgressBar updateProgressBar = new JProgressBar();
    private final JLabel updateLabel = new JLabel(Resource.getTextElement("update.checking"));

    public UpdateFrame() {

        super(Resource.getTextElement("update.title"));

        // Label
        updateLabel.setAlignmentX(CENTER_ALIGNMENT);
        updatePanel.add(updateLabel);

        updatePanel.add(Box.createRigidArea(new Dimension(500, 0)));

        // Progress bar
        updateProgressBar.setIndeterminate(true);
        updateBarHolder.add(updateProgressBar);
        updatePanel.add(updateBarHolder);

        // Ok button
        // Button holder
        JPanel updateButtonHolder = new JPanel();
        updateButtonHolder.setLayout(new BoxLayout(updateButtonHolder, BoxLayout.X_AXIS));
        updatePanel.add(updateButtonHolder);
        // Button
        JButton updateOK = new JButton(Resource.getTextElement("settings.ok"));
        updateButtonHolder.add(updateOK);
        updateOK.addActionListener(event -> UpdateFrame.this.dispose());


        // Panel settings
        updatePanel.setLayout(new BoxLayout(updatePanel, BoxLayout.Y_AXIS));
        updatePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // frame settings
        this.setResizable(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(updatePanel);
        this.pack();
    }

    public void showFrame(JFrame snpFerret) {

        this.setLocationRelativeTo(snpFerret);
        this.setVisible(true);

        if (!checkedForUpdate) {
            // We don't need to check again for updates
            checkedForUpdate = true;

            // We set the update checker
            final UpdateChecker updateWorker = new UpdateChecker();
            updateWorker.addPropertyChangeListener(propertyChangeEvent -> {
                // When check is done
                if (propertyChangeEvent.getPropertyName().equals("state")
                        && propertyChangeEvent.getNewValue() == SwingWorker.StateValue.DONE) {

                    String updateReason = updateWorker.getUpdateMessage();
                    Boolean urgentUpdate = updateWorker.getUrgentUpdate();
                    Boolean needUpdate = updateWorker.getNeedUpdate();

                    // If the update is important
                    if (urgentUpdate || needUpdate) {
                        // We print update reason
                        updateLabel.setText(updateReason);

                        // And we replace the progress bar by a label and an update link
                        // - Removes the progress bar
                        updateBarHolder.remove(updateProgressBar);
                        // - Adds the label
                        JLabel updateFerretLabel =
                                new JLabel(Resource.getTextElement("update.msg"));
                        updateBarHolder.add(updateFerretLabel);
                        // - Adds the link
                        String linkText = Resource.getTextElement("update.link");
                        LinkLabel ferretUpdateLink = new LinkLabel(linkText);
                        ferretUpdateLink.setBackgroundColor(updatePanel.getBackground());
                        ferretUpdateLink.init();
                        ferretUpdateLink.setAlignmentX(LEFT_ALIGNMENT);
                        ferretUpdateLink.setMaximumSize(ferretUpdateLink.getPreferredSize());
                        updateBarHolder.add(ferretUpdateLink);
                        // - Refresh the display
                        updateBarHolder.repaint();
                    } else {
                        // We remove the update label
                        updateLabel.setText("");
                        // And we replace the progress bar by the update reason
                        updateBarHolder.remove(updateProgressBar);
                        updateBarHolder.add(new JLabel(updateReason));
                    }
                }
            });

            // We launch the update check
            updateWorker.execute();
        }
    }
}
