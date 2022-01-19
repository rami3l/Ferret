package fr.ferret.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import fr.ferret.FerretMain;
import fr.ferret.view.utils.Resource;
import lombok.Getter;

/**
 * The locus panel of Ferret <br>
 * Selection of the chromosome and genes parameters
 */
@Getter
public class LocusPanel extends JPanel {

    private final JComboBox<String> chromosomeList;
    private final JTextField inputStart;
    private final JTextField inputEnd;
    private final JLabel titleLabel;

    public LocusPanel() {
        // Labels

        titleLabel = new JLabel(
                Resource.getTextElement(
                        "locus.input." + FerretMain.getConfig().getSelectedHumanGenome().name()),
                SwingConstants.LEFT);
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 24));
        titleLabel.setForeground(new Color(18, 0, 127));
        JLabel helpLabel = new JLabel(Resource.getTextElement("locus.help"), SwingConstants.CENTER);

        // Input panel

        JPanel inputPanel = new JPanel();
        GridBagLayout inputPanelLayout = new GridBagLayout();
        inputPanel.setLayout(inputPanelLayout);

        JLabel labChromosome = new JLabel(Resource.getTextElement("locus.chromosome"));
        String[] chromosomes = {" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22"};

        // Create the combo box, select item at index 4.
        // Indices start at 0, so 4 specifies the pig.
        chromosomeList = new JComboBox<>(chromosomes);
        chromosomeList.setSelectedIndex(0);

        JLabel labStart = new JLabel(Resource.getTextElement("locus.start"));
        JLabel labEnd = new JLabel(Resource.getTextElement("locus.end"));

        inputStart = new JTextField();
        inputEnd = new JTextField();

        GridBagConstraints c = new GridBagConstraints();

        // natural height, maximum width
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.3;
        c.gridx = 1;
        c.gridy = 2;
        labChromosome.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        labChromosome.setFont(new Font(labChromosome.getFont().getFontName(), Font.PLAIN, 16));
        inputPanel.add(labChromosome, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.8;
        c.gridx = 2;
        c.gridy = 2;
        inputPanel.add(chromosomeList, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.3;
        c.gridx = 1;
        c.gridy = 3;
        labStart.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        labStart.setFont(new Font(labStart.getFont().getFontName(), Font.PLAIN, 16));
        inputPanel.add(labStart, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.8;
        c.gridx = 2;
        c.gridy = 3;
        inputPanel.add(inputStart, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.3;
        c.gridx = 1;
        c.gridy = 4;
        labEnd.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        labEnd.setFont(new Font(labEnd.getFont().getFontName(), Font.PLAIN, 16));
        inputPanel.add(labEnd, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.8;
        c.gridx = 2;
        c.gridy = 4;
        inputPanel.add(inputEnd, c);

        // Add elements

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(helpLabel, BorderLayout.SOUTH);

        // Borders
        setBorder(BorderFactory.createLineBorder(new Color(131, 55, 192, 140), 4));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        helpLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    }

}
