package fr.ferret.view.panel;

import static fr.ferret.view.utils.GuiUtils.addToPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import fr.ferret.controller.BrowseFileButtonListener;
import fr.ferret.utils.Resource;
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
        JLabel helpLabel =
                new JLabel(Resource.getTextElement("variant.help"), SwingConstants.CENTER);

        // Add the 3 parts defined above to the layout
        this.setLayout(new BorderLayout());
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
     * Generates the title of the variant panel
     * 
     * @return the title label
     */
    private JLabel generateTitle() {
        JLabel titleLabel =
                new JLabel(Resource.getTextElement("variant.input"), SwingConstants.LEFT);
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 24));
        titleLabel.setForeground(new Color(18, 0, 127));
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
        browseButton.setBackground(new Color(201, 157, 240));

        JLabel selectedFile = new JLabel(Resource.getTextElement("variant.selectfile"));
        selectedFile.setFont(new Font(selectedFile.getFont().getFontName(), Font.PLAIN, 13));

        fileSelector = new BrowseFileButtonListener(this, browseButton, selectedFile);

        // Add the elements defined above to the input panel
        addToPanel(inputPanel, variantIdField, 0.8, 1, 1);
        addToPanel(inputPanel, orLabel, 0.2, 2, 1);
        addToPanel(inputPanel, browseButton, 0.6, 3, 1);
        addToPanel(inputPanel, selectedFile, 0.6, 3, 2);
        addToPanel(inputPanel, includingVariantSubPanel, 0.7, 1, 2);

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
        addToPanel(includingVariantSubPanel, checkbox, 0.002, 1, 1);
        addToPanel(includingVariantSubPanel, bpField, 0.4, 2, 1);
        addToPanel(includingVariantSubPanel, bpLabel, 0.3, 3, 1);

        return includingVariantSubPanel;
    }

}
