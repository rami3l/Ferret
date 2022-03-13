package fr.ferret.view.panel.inputs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
 * The gene panel of Ferret <br>
 * Selection of the genes parameters
 */
@Getter
public class GenePanel extends JPanel {

    private JTextField inputField;
    private transient BrowseFileButtonListener fileSelector;

    /**
     * Creates the gene panel
     */
    public GenePanel() {

        /* --- Title --- */
        JLabel titleLabel = generateTitle();

        /* --- Input panel --- */
        JPanel inputPanel = generateInputPanel();

        /* --- Help section --- */
        JTextPane helpPane = new JTextPane();
        helpPane.setContentType("text/html");
        helpPane.setText(Resource.getTextElement("gene.help"));
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

    /**
     * Generates the input part of the gene panel
     * 
     * @return the input panel
     */
    private JPanel generateInputPanel() {

        JPanel inputPanel = new JPanel(new GridBagLayout());

        // Input gene
        inputField = new JTextField();


        // Or Label
        JLabel labOr = new JLabel(Resource.getTextElement("gene.or"));
        labOr.setBorder(BorderFactory.createEmptyBorder(0, 130, 0, 10));
        labOr.setFont(new Font(labOr.getFont().getFontName(), Font.PLAIN, 16));

        // File selection
        JButton browseButton = new JButton(Resource.getTextElement("gene.browse"));
        browseButton.setPreferredSize(new Dimension(200, 30));
        browseButton.setBackground(Resource.BUTTON_COLOR);

        JLabel selectedFile = new JLabel(Resource.getTextElement("gene.selectfile"));
        selectedFile.setFont(new Font(selectedFile.getFont().getFontName(), Font.PLAIN, 13));

        fileSelector = new BrowseFileButtonListener(this, browseButton, selectedFile);

        // Add the elements defined above to the input panel
        GuiUtils.addToPanel(inputPanel, inputField, 0.8, 1, 1);
        GuiUtils.addToPanel(inputPanel, labOr, 0.3, 2, 1);
        GuiUtils.addToPanel(inputPanel, browseButton, 0.3, 3, 1);
        GuiUtils.addToPanel(inputPanel, selectedFile, 0.3, 3, 2);

        return inputPanel;
    }

}
