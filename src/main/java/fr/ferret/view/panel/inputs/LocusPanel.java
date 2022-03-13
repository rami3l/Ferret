package fr.ferret.view.panel.inputs;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import fr.ferret.utils.Resource;
import fr.ferret.view.utils.GuiUtils;
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
        JTextPane helpPane = new JTextPane();
        helpPane.setContentType("text/html");
        helpPane.setText(Resource.getTextElement("locus.help"));
        helpPane.setBackground(null);
        helpPane.setEditable(false);
        StyledDocument styledHelpPane = helpPane.getStyledDocument();
        // Set the font
        MutableAttributeSet attrs = helpPane.getInputAttributes();
        StyleConstants.setFontFamily(attrs, Resource.HELP_LABEL_FONT.getFamily());
        StyleConstants.setFontSize(attrs, Resource.HELP_LABEL_FONT.getSize());
        styledHelpPane.setCharacterAttributes(0, styledHelpPane.getLength() + 1, attrs, false);
        // Center the text
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        styledHelpPane.setParagraphAttributes(0, styledHelpPane.getLength() + 1, center, false);

        // Add the 3 parts defined above to the layout
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(helpPane, BorderLayout.SOUTH);

        // Set the borders
        setBorder(BorderFactory.createLineBorder(Resource.PANEL_BORDER_COLOR, 4));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        helpPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
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
        GuiUtils.addToPanel(inputPanel, labChromosome, 0.3, 1, 2);
        GuiUtils.addToPanel(inputPanel, chromosomeList, 0.8, 2, 2);
        GuiUtils.addToPanel(inputPanel, labStart, 0.3, 1, 3);
        GuiUtils.addToPanel(inputPanel, labEnd, 0.3, 1, 4);
        GuiUtils.addToPanel(inputPanel, inputStart, 0.8, 2, 3);
        GuiUtils.addToPanel(inputPanel, inputEnd, 0.8, 2, 4);

        return inputPanel;
    }

}
