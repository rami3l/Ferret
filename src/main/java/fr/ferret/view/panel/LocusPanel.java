package fr.ferret.view.panel;

import static fr.ferret.view.utils.GuiUtils.addToPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import fr.ferret.utils.Resource;
import lombok.Getter;

/**
 * The locus panel of Ferret <br>
 * Selection of the chromosome and genes parameters
 */
@Getter
public class LocusPanel extends JPanel {

    private JComboBox<String> chromosomeList;
    private JTextField inputStart;
    private JTextField inputEnd;
    private final JLabel titleLabel;

    /**
     * Creates the locus panel
     */
    public LocusPanel() {

        /* --- Title --- */
        titleLabel = generateTitle();

        /* --- Input panel -- */
        JPanel inputPanel = generateInputPanel();

        /* --- Help section --- */
        JLabel helpLabel = new JLabel(Resource.getTextElement("locus.help"), SwingConstants.CENTER);

        // Add the 3 parts defined above to the layout
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(helpLabel, BorderLayout.SOUTH);

        // Set the borders
        setBorder(BorderFactory.createLineBorder(new Color(131, 55, 192, 140), 4));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        helpLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    }

    /**
     * Generates the title of the locus panel
     * 
     * @return the title label
     */
    private JLabel generateTitle() {
        String text = Resource
                .getTextElement("locus.input." + Resource.CONFIG.getSelectedHumanGenome().name());
        JLabel title = new JLabel(text, SwingConstants.LEFT);
        title.setFont(new Font("Calibri", Font.BOLD, 24));
        title.setForeground(new Color(18, 0, 127));
        return title;
    }

    /**
     * Generates the input panel of the locus panel
     * 
     * @return the input panel
     */
    private JPanel generateInputPanel() {

        JPanel inputPanel = new JPanel(new GridBagLayout());

        // Chromosome selection
        JLabel labChromosome = new JLabel(Resource.getTextElement("locus.chromosome"));
        labChromosome.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        labChromosome.setFont(new Font(labChromosome.getFont().getFontName(), Font.PLAIN, 16));

        String[] chromosomes = {" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22"};

        // Create the combo box, select item at index 4.
        // Indices start at 0, so 4 specifies the pig.
        chromosomeList = new JComboBox<>(chromosomes);
        chromosomeList.setSelectedIndex(0);

        // Start position selection
        JLabel labStart = new JLabel(Resource.getTextElement("locus.start"));
        labStart.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        labStart.setFont(new Font(labStart.getFont().getFontName(), Font.PLAIN, 16));

        inputStart = new JTextField();

        // End position selection
        JLabel labEnd = new JLabel(Resource.getTextElement("locus.end"));
        labEnd.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        labEnd.setFont(new Font(labEnd.getFont().getFontName(), Font.PLAIN, 16));

        inputEnd = new JTextField();

        // Add the elements defined above to the input panel
        addToPanel(inputPanel, labChromosome, 0.3, 1, 2);
        addToPanel(inputPanel, chromosomeList, 0.8, 2, 2);
        addToPanel(inputPanel, labStart, 0.3, 1, 3);
        addToPanel(inputPanel, labEnd, 0.3, 1, 4);
        addToPanel(inputPanel, inputStart, 0.8, 2, 3);
        addToPanel(inputPanel, inputEnd, 0.8, 2, 4);

        return inputPanel;
    }

}
