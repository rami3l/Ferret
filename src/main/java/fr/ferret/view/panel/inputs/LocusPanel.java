package fr.ferret.view.panel.inputs;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.ferret.utils.Resource;
import fr.ferret.view.panel.inputs.common.InputPanel;
import fr.ferret.view.utils.GuiUtils;
import lombok.Getter;

/**
 * The locus panel of Ferret <br>
 * Selection of the chromosome and genes parameters
 */
@Getter
public class LocusPanel extends InputPanel {

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
        var help = generateHelpSection("locus.help");

        // Adds the 3 parts defined above to the contentPanel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(inputPanel, BorderLayout.CENTER);
        contentPanel.add(help, BorderLayout.SOUTH);

        // Sets the borders and adds the content to the panel
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        setBorder(BorderFactory.createLineBorder(Resource.PANEL_BORDER_COLOR, 4));
        add(contentPanel);
    }

    /**
     * Generates the title of the locus panel
     * 
     * @return the title label
     */
    private JLabel generateTitle() {
        String text = Resource
                .getTextElement("locus.input." + Resource.config().getSelectedHumanGenome());
        JLabel title = new JLabel(text, SwingConstants.LEFT);
        title.setFont(Resource.TITLE_FONT);
        title.setForeground(Resource.TITLE_COLOR);
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
        labChromosome.setFont(new Font(labChromosome.getFont().getFontName(), Font.PLAIN, 18));

        String[] chromosomes = {" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22"};

        // Create the combo box, select item at index 4.
        // Indices start at 0, so 4 specifies the pig.
        chromosomeList = new JComboBox<>(chromosomes);
        chromosomeList.setSelectedIndex(0);

        // Start position selection
        JLabel labStart = new JLabel(Resource.getTextElement("locus.start"));
        labStart.setFont(new Font(labStart.getFont().getFontName(), Font.PLAIN, 18));

        inputStart = new JTextField();

        // End position selection
        JLabel labEnd = new JLabel(Resource.getTextElement("locus.end"));
        labEnd.setFont(new Font(labEnd.getFont().getFontName(), Font.PLAIN, 18));

        inputEnd = new JTextField();

        // Add the elements defined above to the input panel
        GuiUtils.addToPanel(inputPanel, labChromosome, 0.3, 1, 2);
        GuiUtils.addToPanel(inputPanel, chromosomeList, 0.8, 2, 2);
        GuiUtils.addToPanel(inputPanel, labStart, 0.3, 1, 3);
        GuiUtils.addToPanel(inputPanel, labEnd, 0.3, 1, 4);
        GuiUtils.addToPanel(inputPanel, inputStart, 0.8, 2, 3);
        GuiUtils.addToPanel(inputPanel, inputEnd, 0.8, 2, 4);

        return inputPanel;
    }

}
