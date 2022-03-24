package fr.ferret.view.panel.inputs;

import fr.ferret.controller.BrowseFileButtonListener;
import fr.ferret.utils.Resource;
import fr.ferret.view.utils.GuiUtils;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * A panel allowing the user to input a text or a file
 */
@Getter
public class FieldOrFilePanel extends JPanel {

    private JTextField inputField;
    protected JTextField bpField;
    private transient BrowseFileButtonListener fileSelector;

    protected FieldOrFilePanel(String titleTextElement, String helpTextElement) {
        /* --- Title --- */
        JLabel titleLabel = generateTitle(titleTextElement);

        /* --- Input panel --- */
        JPanel inputPanel = generateInputPanel();

        /* --- Help section --- */
        JTextPane helpPane = generateHelpSection(helpTextElement);
        helpPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        // Adds the 3 parts defined above to the contentPanel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(inputPanel, BorderLayout.CENTER);
        contentPanel.add(helpPane, BorderLayout.SOUTH);

        // Sets the borders and adds the content to the panel
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        setBorder(BorderFactory.createLineBorder(Resource.PANEL_BORDER_COLOR, 4));
        add(contentPanel);
    }

    /**
     * Generates the input part of the gene panel
     *
     * @return the input panel
     */
    protected JPanel generateInputPanel() {

        JPanel inputPanel = new JPanel(new GridBagLayout());

        // Input gene
        inputField = new JTextField();

        // Including variants (placed in a sub panel)
        bpField = new JTextField();
        JPanel includingVariantSubPanel = includingVariantsPanel();

        // Or Label
        JLabel orLabel = new JLabel(Resource.getTextElement("input.or"));
        orLabel.setBorder(BorderFactory.createEmptyBorder(0, 130, 0, 10));
        orLabel.setFont(new Font(orLabel.getFont().getFontName(), Font.PLAIN, 16));

        // File selection
        JButton browseButton = new JButton(Resource.getTextElement("input.browse"));
        browseButton.setPreferredSize(new Dimension(200, 30));
        browseButton.setBackground(Resource.BUTTON_COLOR);

        JLabel selectedFile = new JLabel(Resource.getTextElement("input.selectfile"));
        selectedFile.setFont(new Font(selectedFile.getFont().getFontName(), Font.PLAIN, 13));

        fileSelector = new BrowseFileButtonListener(this, browseButton, selectedFile);

        // Add the elements defined above to the input panel
        GuiUtils.addToPanel(inputPanel, inputField, 0.8, 1, 1);
        GuiUtils.addToPanel(inputPanel, includingVariantSubPanel, 0.7, 1, 2);
        GuiUtils.addToPanel(inputPanel, orLabel, 0.2, 2, 1);
        GuiUtils.addToPanel(inputPanel, browseButton, 0.4, 3, 1);
        GuiUtils.addToPanel(inputPanel, selectedFile, 0.4, 3, 2);

        return inputPanel;
    }

    /**
     * Generates the 'Including variants' part of the input panel
     */
    public JPanel includingVariantsPanel() {

        JPanel includingVariantSubPanel = new JPanel(new GridBagLayout());

        // Including variants label
        JLabel bpLabel = new JLabel(Resource.getTextElement("input.window.label"));
        bpLabel.setFont(new Font(bpLabel.getFont().getFontName(), Font.PLAIN, 13));


        // bp Label
        JLabel bpUnit = new JLabel(" " + Resource.getTextElement("input.window.unit"));
        bpUnit.setFont(new Font(bpUnit.getFont().getFontName(), Font.PLAIN, 13));

        // Add the elements defined above to the input panel
        GuiUtils.addToPanel(includingVariantSubPanel, bpLabel, 0.1, 1, 1);
        GuiUtils.addToPanel(includingVariantSubPanel, bpField, 0.4, 2, 1);
        GuiUtils.addToPanel(includingVariantSubPanel, bpUnit, 0.3, 3, 1);

        return includingVariantSubPanel;
    }

    protected JTextPane generateHelpSection(String textElement) {
        JTextPane helpPane = new JTextPane();
        helpPane.setContentType("text/html");
        helpPane.setText(Resource.getTextElement(textElement));
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
        return  helpPane;
    }

    protected JLabel generateTitle(String textElement) {
        JLabel titleLabel = new JLabel(Resource.getTextElement(textElement), SwingConstants.LEFT);
        titleLabel.setFont(Resource.TITLE_FONT);
        titleLabel.setForeground(Resource.TITLE_COLOR);
        return titleLabel;
    }

}
