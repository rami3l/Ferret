package fr.ferret.view;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import fr.ferret.utils.Resource;
import fr.ferret.view.panel.RegionPanel;
import fr.ferret.view.panel.RunPanel;
import fr.ferret.view.panel.header.MenuPanel;
import fr.ferret.view.panel.inputs.GenePanel;
import fr.ferret.view.panel.inputs.LocusPanel;
import fr.ferret.view.panel.inputs.VariantPanel;
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
    private final RunPanel runPanel;
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
        Optional<BufferedImage> icon = Resource.getImage("/img/ferret.jpg");
        icon.ifPresent(this::setIconImage);

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

        // Creates the run panel
        runPanel = new RunPanel(this);
        panel.add(runPanel);

        // Window settings
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

}
