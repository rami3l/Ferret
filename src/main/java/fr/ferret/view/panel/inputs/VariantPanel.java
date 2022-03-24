package fr.ferret.view.panel.inputs;

import fr.ferret.utils.Resource;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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
        JTextPane helpPane = new JTextPane();
        helpPane.setContentType("text/html");
        helpPane.setText(Resource.getTextElement("variant.help"));
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
        this.setLayout(new BorderLayout());
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
