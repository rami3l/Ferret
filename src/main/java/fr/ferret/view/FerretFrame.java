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
import fr.ferret.view.panel.GenePanel;
import fr.ferret.view.panel.LocusPanel;
import fr.ferret.view.panel.RegionPanel;
import fr.ferret.view.panel.RunPanel;
import fr.ferret.view.panel.VariantPanel;
import fr.ferret.view.panel.header.MenuPanel;
import fr.ferret.view.utils.Resource;
import lombok.Getter;

/**
 * Main Ferret frame
 */
@Getter
public class FerretFrame extends JFrame {

    private static final Logger LOG = Logger.getLogger(FerretFrame.class.getName());

    private final MenuPanel headerPanel;
    private final JTabbedPane inputTabs;
    private final LocusPanel locusPanel;
    private final RegionPanel regionPanel;
    private final RunPanel runPanel;
    private final GenePanel genePanel;
    private final VariantPanel variantPanel;

    public FerretFrame() {
        // Set look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            LOG.log(Level.WARNING, "Failed to set ferret look and feel !", e);
        }
        // Set icon
        Optional<BufferedImage> icon = Resource.getImage("/img/ferret.jpg");
        if (icon.isPresent()) {
            setIconImage(icon.get());
        }

        headerPanel = new MenuPanel(this);
        locusPanel = new LocusPanel();
        genePanel = new GenePanel();
        variantPanel = new VariantPanel();
        regionPanel = new RegionPanel();
        runPanel = new RunPanel(this);

        // Créer le conteneur des onglets
        inputTabs = new JTabbedPane();
        // Définir la position de conteneur d'onglets
        inputTabs.setBounds(40, 20, 300, 300);
        // Associer chaque panneau à l'onglet correspondant
        inputTabs.add("Locus", locusPanel);
        inputTabs.add("Gene", genePanel);
        inputTabs.add("Variant", variantPanel);

        setTitle("Ferret v3");
        setJMenuBar(headerPanel);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(inputTabs);
        panel.add(regionPanel);
        panel.add(runPanel);

        // affecte le panneau a la fenetre
        setContentPane(panel);
        pack();

        // maFrame.add(new JButton("Button 1")); //SwingConstants.CENTER
        // setSize(800,790);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
