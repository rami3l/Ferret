package fr.ferret.view.panel.inputs;

import fr.ferret.utils.Resource;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * The gene panel of Ferret <br>
 * Selection of the genes parameters
 */
@Getter
public class GenePanel extends FieldOrFilePanel {


    /**
     * Creates the gene panel
     */
    public GenePanel() {

        /* --- Title --- */
        JLabel titleLabel = generateTitle();

        /* --- Input panel --- */
        JLabel helpLabel1 = new JLabel(Resource.getTextElement("gene.help"), SwingConstants.CENTER);

        /* --- Help section --- */
        JPanel inputPanel = generateInputPanel();

        // Add the 3 parts defined above to the layout
        this.setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(helpLabel1, BorderLayout.SOUTH);

        // Set the borders
        setBorder(BorderFactory.createLineBorder(Resource.PANEL_BORDER_COLOR, 4));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        helpLabel1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    }

    /**
     * Generates the title of the gene panel
     * 
     * @return the title label
     */
    private JLabel generateTitle() {
        JLabel titleLabel = new JLabel(Resource.getTextElement("gene.input"), SwingConstants.LEFT);
        titleLabel.setFont(Resource.TITLE_FONT);
        titleLabel.setForeground(Resource.TITLE_COLOR);
        return titleLabel;
    }

}
