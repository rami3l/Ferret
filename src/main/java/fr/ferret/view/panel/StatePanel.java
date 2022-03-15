package fr.ferret.view.panel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.Duration;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.ferret.controller.state.Message;
import fr.ferret.model.state.PublishingStateProcessus;
import fr.ferret.model.state.State;
import fr.ferret.utils.Resource;
import fr.ferret.view.utils.GuiUtils;
import lombok.AllArgsConstructor;
import lombok.Setter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public class StatePanel extends JPanel {
    /**
     * the label describing the current state (downloading header, lines, etc.)
     */
    private final JLabel stateLabel;
    /**
     * The spinner displayed while downloading
     */
    private final JLabel spinner;
    /**
     * The button to open the download location
     */
    private final JButton openButton;
    private transient Disposable destroyAction = null;
    private boolean completed = false;
    private boolean canDestroy = true;
    private final File downloadLocation;
    @Setter
    private PublishingStateProcessus<?> associatedProcessus;

    public StatePanel(String text, File downloadLocation) {

        this.downloadLocation = downloadLocation;

        stateLabel = new JLabel(text);
        spinner = new JLabel();
        Resource.getIcon("/img/loading.gif").ifPresentOrElse(spinner::setIcon,
                () -> spinner.setText("..."));
        spinner.setToolTipText(Resource.getTextElement("tooltip.downloading"));

        openButton = new JButton();
        openButton.setVisible(false);
        openButton.setSize(10, 10);

        this.add(stateLabel);
        this.add(spinner);
        this.add(openButton);

        var mouseListener = new MousePanelListener(this);
        addMouseListener(mouseListener);
        stateLabel.addMouseListener(mouseListener);
        spinner.addMouseListener(mouseListener);
        openButton.addMouseListener(mouseListener);
        spinner.addMouseListener(new MouseClickListener());
    }

    public void setState(State state) {
        setMessage(Message.from(state));
    }

    public void setMessage(Message message) {
        stateLabel.setText(message.getText());
        stateLabel.setToolTipText(message.getTooltip());
        this.setToolTipText(message.getTooltip());
    }

    public void error() {
        setMessage(new Message("error.toast", null, null));
        complete(false);
    }

    public void complete() {
        complete(true);
    }

    private void complete(boolean ok) {
        // When the download is complete, hides spinner and makes the open button visible
        spinner.setVisible(false);
        if (ok && downloadLocation != null) {
            openButton.setToolTipText(Resource.getTextElement("tooltip.openDownload"));
            Resource.getIcon("/img/open-folder.png").ifPresentOrElse(openButton::setIcon,
                    () -> openButton.setText(Resource.getTextElement("button.open")));
            openButton.addActionListener(new ButtonListener());
            openButton.setVisible(true);
        }
        completed = true;
        if (canDestroy)
            destroyAction = startDestroyAction();
    }

    // TODO: opacity change is not fluent
    private Disposable startDestroyAction() {
        return Flux.range(0, 26).delaySubscription(Duration.ofSeconds(3))
                .delayElements(Duration.ofMillis(200))
                .doOnNext(i -> setForegroundOpacity(stateLabel, 250 - 10 * i))
                .doOnComplete(this::destroy).subscribe();
    }

    private void destroy() {
        this.setVisible(false);
        // TODO: remove this from the StatesPanel
    }


    private void setForegroundOpacity(JComponent component, int alpha) {
        component.setForeground(withOpacity(component.getForeground(), alpha));
    }

    private Color withOpacity(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    @AllArgsConstructor
    private class MousePanelListener extends MouseAdapter {

        private final JPanel panel;

        @Override
        public void mouseEntered(MouseEvent e) {
            panel.setBackground(panel.getBackground().darker());
            canDestroy = false;
            if (destroyAction != null) {
                destroyAction.dispose();
                setForegroundOpacity(stateLabel, 255);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            panel.setBackground(panel.getBackground().brighter());
            if (completed) {
                destroyAction = startDestroyAction();
            }
        }
    }

    private class MouseClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (associatedProcessus != null) {
                associatedProcessus.cancel();
            }
        }
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            GuiUtils.openFileLocation(downloadLocation);
        }
    }
}
