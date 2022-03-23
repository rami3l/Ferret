package fr.ferret.view.panel.inputs;

import fr.ferret.utils.Resource;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * The variant panel of Ferret <br>
 * Selection of the gene variants parameters
 */
@Getter
public class VariantPanel extends FieldOrFilePanel {

    /**
     * Creates the variant panel
     */
    public VariantPanel() {

        /* --- Title --- */
        JLabel titleLabel = generateTitle();

        /* --- Input panel --- */
        JPanel inputPanel = generateInputPanel();

        /* --- Help section --- */
        JLabel helpLabel =
                new JLabel(Resource.getTextElement("variant.help"), SwingConstants.CENTER);

        // Add the 3 parts defined above to the layout
        this.setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(helpLabel, BorderLayout.SOUTH);

        // Set the borders
        setBorder(BorderFactory.createLineBorder(Resource.PANEL_BORDER_COLOR, 4));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        helpLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    }

    /**
     * Generates the title of the variant panel
     * 
     * @return the title label
     */
    private JLabel generateTitle() {
        JLabel titleLabel =
                new JLabel(Resource.getTextElement("variant.input"), SwingConstants.LEFT);
        titleLabel.setFont(Resource.TITLE_FONT);
        titleLabel.setForeground(Resource.TITLE_COLOR);
        return titleLabel;
    }

}
