package fr.ferret.view.panel;

import fr.ferret.view.utils.GuiUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.Duration;

import static fr.ferret.utils.Resource.getIcon;
import static fr.ferret.utils.Resource.getTextElement;

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

    public StatePanel(String text, File downloadLocation) {

        this.downloadLocation = downloadLocation;

        stateLabel = new JLabel(text);
        spinner = new JLabel();
        getIcon("/img/loading.gif").ifPresentOrElse(spinner::setIcon, () -> spinner.setText("..."));
        spinner.setToolTipText(getTextElement("tooltip.downloading"));

        openButton = new JButton();
        openButton.setVisible(false);
        openButton.setSize(10, 10);

        this.add(stateLabel);
        this.add(spinner);
        this.add(openButton);

        addMouseListener(new MousePanelListener());
    }

    public void setState(String text) {
        stateLabel.setText(text);
    }

    public void complete() {
        // When the download is complete, hides spinner and makes the open button visible
        spinner.setVisible(false);
        if(downloadLocation != null) {
            openButton.setToolTipText(getTextElement("tooltip.openDownload"));
            getIcon("/img/open-folder.png").ifPresentOrElse(openButton::setIcon, () -> openButton.setText(getTextElement("button.open")));
            openButton.addActionListener(new ButtonListener());
            openButton.setVisible(true);
        }
        completed = true;
        if (canDestroy)
            destroyAction = startDestroyAction();
        //openButton.setBorder(null);
        //openButton.setBorder(BorderFactory.createLineBorder(withOpacity(openButton.getBackground(), 1), 3));
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

    private class MousePanelListener extends MouseAdapter {
        @Override public void mouseEntered(MouseEvent e) {
            // TODO: highlight the panel
            canDestroy = false;
            if (destroyAction != null) {
                destroyAction.dispose();
                setForegroundOpacity(stateLabel, 255);
            }
        }

        @Override public void mouseExited(MouseEvent e) {
            // TODO: unhighlight the panel
            if (completed) {
                destroyAction = startDestroyAction();
            }
        }
    }

    private class ButtonListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent actionEvent) {
            GuiUtils.openFileLocation(downloadLocation);
        }
    }
}
