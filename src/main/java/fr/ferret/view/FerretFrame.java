package fr.ferret.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import fr.ferret.model.state.PublishingStateProcessus;
import fr.ferret.utils.Resource;
import fr.ferret.view.panel.BottomPanel;
import fr.ferret.view.panel.RegionPanel;
import fr.ferret.view.panel.header.MenuPanel;
import fr.ferret.view.panel.inputs.GenePanel;
import fr.ferret.view.panel.inputs.LocusPanel;
import fr.ferret.view.panel.inputs.VariantPanel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Main Ferret frame
 */
@Getter
public class FerretFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(FerretFrame.class.getName());

    private final MenuPanel headerPanel;
    private final JTabbedPane inputTabs;
    private final LocusPanel locusPanel;
    private final RegionPanel regionPanel;
    private final BottomPanel bottomPanel;
    private final GenePanel genePanel;
    private final VariantPanel variantPanel;

    public FerretFrame() {
        // Set look (for example dark/white mode)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            logger.log(Level.WARNING, "Failed to set ferret look and feel !", e);
        }

        // Set icon
        Resource.getImage("/img/ferret.jpg").ifPresent(this::setIconImage);

        // Set the window title
        setTitle("Ferret v3");

        // Creates the menu panel
        headerPanel = new MenuPanel(this);
        setJMenuBar(headerPanel);

        // Creates the content panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        setContentPane(panel);

        // Creates the 3 input panels
        locusPanel = new LocusPanel();
        genePanel = new GenePanel();
        variantPanel = new VariantPanel();

        // Creates 3 tabs in a tab container for these 3 input panels
        inputTabs = new JTabbedPane();
        inputTabs.setBounds(40, 20, 300, 300);
        inputTabs.add("Locus", locusPanel);
        inputTabs.add("Gene", genePanel);
        inputTabs.add("Variant", variantPanel);
        panel.add(inputTabs);

        // Creates the region panel
        regionPanel = new RegionPanel();
        panel.add(regionPanel);

        // Creates the bottom panel
        bottomPanel = new BottomPanel(this);
        panel.add(bottomPanel);

        // Window settings
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new CloseListener(this));
        pack();
    }

    @AllArgsConstructor
    private static class CloseListener extends WindowAdapter {

        private JFrame parent;

        @Override
        public void windowClosing(WindowEvent event) {
            if(PublishingStateProcessus.getCurrentProcessusList().isEmpty() || confirmClose()) {
                System.exit(0);
            }
        }

        private boolean confirmClose() {
            var confirmMessage = Resource.getTextElement("close.confirmMessage");
            var confirmTitle = Resource.getTextElement("close.confirmTitle");
            return JOptionPane.showConfirmDialog(parent, confirmMessage, confirmTitle,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        }
    }

}
