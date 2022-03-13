package fr.ferret.view.panel.inputs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import fr.ferret.controller.BrowseFileButtonListener;
import fr.ferret.utils.Resource;
import fr.ferret.view.utils.GuiUtils;
import lombok.Getter;

/**
 * The variant panel of Ferret <br>
 * Selection of the gene variants parameters
 */
@Getter
public class VariantPanel extends JPanel {

    private JTextField variantIdField;
    private JCheckBox checkbox;
    private JTextField bpField;
    private BrowseFileButtonListener fileSelector;

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

    /**
     * Generates the input part of the variant panel
     * 
     * @return the input panel
     */
    private JPanel generateInputPanel() {

        JPanel inputPanel = new JPanel(new GridBagLayout());

        // Variant Id
        variantIdField = new JTextField();

        // Including variants (placed in a subpanel)
        JPanel includingVariantSubPanel = generateIncludingVariantPanel();

        // Or Label
        JLabel orLabel = new JLabel(Resource.getTextElement("variant.or"));
        orLabel.setBorder(BorderFactory.createEmptyBorder(0, 130, 0, 10));
        orLabel.setFont(new Font(orLabel.getFont().getFontName(), Font.PLAIN, 16));

        // File selection
        JButton browseButton = new JButton(Resource.getTextElement("variant.browse"));
        browseButton.setPreferredSize(new Dimension(200, 30));
        browseButton.setBackground(Resource.BUTTON_COLOR);

        JLabel selectedFile = new JLabel(Resource.getTextElement("variant.selectfile"));
        selectedFile.setFont(new Font(selectedFile.getFont().getFontName(), Font.PLAIN, 13));

        fileSelector = new BrowseFileButtonListener(this, browseButton, selectedFile);

        // Add the elements defined above to the input panel
        GuiUtils.addToPanel(inputPanel, variantIdField, 0.8, 1, 1);
        GuiUtils.addToPanel(inputPanel, orLabel, 0.2, 2, 1);
        GuiUtils.addToPanel(inputPanel, browseButton, 0.6, 3, 1);
        GuiUtils.addToPanel(inputPanel, selectedFile, 0.6, 3, 2);
        GuiUtils.addToPanel(inputPanel, includingVariantSubPanel, 0.7, 1, 2);

        return inputPanel;
    }

    /**
     * Generates the 'Including variants' part of the input panel
     * 
     * @return the 'Including variants' subpanel
     */
    private JPanel generateIncludingVariantPanel() {

        JPanel includingVariantSubPanel = new JPanel(new GridBagLayout());

        // Including variants checbox
        checkbox = new JCheckBox(Resource.getTextElement("variant.bpcheckbox"));
        checkbox.setFont(new Font(checkbox.getFont().getFontName(), Font.PLAIN, 13));

        // bp Field
        bpField = new JTextField();

        // bp Label
        JLabel bpLabel = new JLabel(Resource.getTextElement("variant.bp"));
        bpLabel.setFont(new Font(bpLabel.getFont().getFontName(), Font.PLAIN, 13));

        // Add the elements defined above to the input panel
        GuiUtils.addToPanel(includingVariantSubPanel, checkbox, 0.002, 1, 1);
        GuiUtils.addToPanel(includingVariantSubPanel, bpField, 0.4, 2, 1);
        GuiUtils.addToPanel(includingVariantSubPanel, bpLabel, 0.3, 3, 1);

        return includingVariantSubPanel;
    }

}
