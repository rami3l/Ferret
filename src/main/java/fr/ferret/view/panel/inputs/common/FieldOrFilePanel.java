package fr.ferret.view.panel.inputs.common;

import fr.ferret.controller.BrowseFileButtonListener;
import fr.ferret.utils.Resource;
import fr.ferret.view.HighlightableTextField;
import fr.ferret.view.utils.GuiUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * A panel allowing the user to input a text or a file
 */
@Getter
public class FieldOrFilePanel extends InputPanel {

    private JTextField inputField;
    protected HighlightableTextField bpField;
    private transient BrowseFileButtonListener fileSelector;

    protected FieldOrFilePanel(String titleTextElement, String helpTextElement) {
        /* --- Title --- */
        JLabel titleLabel = generateTitle(titleTextElement);

        /* --- Input panel --- */
        JPanel inputPanel = generateInputPanel();

        /* --- Help section --- */
        JTextPane help = generateHelpSection(helpTextElement);

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
     * Generates the input part of the gene panel
     *
     * @return the input panel
     */
    protected JPanel generateInputPanel() {

        JPanel inputPanel = new JPanel(new GridBagLayout());

        // Input gene
        inputField = new JTextField();

        // Including variants (placed in a sub panel)
        bpField = new HighlightableTextField(2);
        JPanel includingVariantSubPanel = includingVariantsPanel();

        // Or Label
        JLabel orLabel = new JLabel(Resource.getTextElement("input.or"));
        orLabel.setBorder(BorderFactory.createEmptyBorder(0, 130, 0, 10));
        orLabel.setFont(new Font(orLabel.getFont().getFontName(), Font.PLAIN, 16));

        // File selection
        JButton browseButton = new JButton(Resource.getTextElement("input.browse"));
        browseButton.setPreferredSize(new Dimension(200, 30));
        browseButton.setBackground(Resource.BUTTON_COLOR);

        JLabel selectedFileLabel = new JLabel(Resource.getTextElement("input.noFileSelected"));
        selectedFileLabel.setFont(new Font(selectedFileLabel.getFont().getFontName(), Font.PLAIN, 14));

        fileSelector = new BrowseFileButtonListener(this, browseButton, selectedFileLabel);

        JButton resetButton = new JButton();
        Resource.getIcon("/img/cancel.png", 20, 20)
            .ifPresentOrElse(resetButton::setIcon, () -> resetButton.setText("X"));
        resetButton.setPreferredSize(new Dimension(25, 25));
        resetButton.addActionListener(actionEvent -> fileSelector.reset());

        JPanel fileSelection = new JPanel();
        fileSelection.add(selectedFileLabel);
        fileSelection.add(resetButton);


        // Add the elements defined above to the input panel
        GuiUtils.addToPanel(inputPanel, inputField, 0.8, 1, 1);
        GuiUtils.addToPanel(inputPanel, includingVariantSubPanel, 0.7, 1, 2);
        GuiUtils.addToPanel(inputPanel, orLabel, 0.2, 2, 1);
        GuiUtils.addToPanel(inputPanel, browseButton, 0.4, 3, 1);
        GuiUtils.addToPanel(inputPanel, fileSelection, 0.4, 3, 2);

        return inputPanel;
    }

    /**
     * Generates the 'Including variants' part of the input panel
     */
    public JPanel includingVariantsPanel() {

        JPanel includingVariantSubPanel = new JPanel(new GridBagLayout());

        // Including variants label
        JLabel bpLabel = new JLabel(Resource.getTextElement("input.window.label"));
        bpLabel.setFont(new Font(bpLabel.getFont().getFontName(), Font.PLAIN, 14));


        // bp Label
        JLabel bpUnit = new JLabel(" " + Resource.getTextElement("input.window.unit"));
        bpUnit.setFont(new Font(bpUnit.getFont().getFontName(), Font.PLAIN, 13));

        // Add the elements defined above to the input panel
        GuiUtils.addToPanel(includingVariantSubPanel, bpLabel, 0.1, 1, 1);
        GuiUtils.addToPanel(includingVariantSubPanel, bpField, 0.4, 2, 1);
        GuiUtils.addToPanel(includingVariantSubPanel, bpUnit, 0.3, 3, 1);

        return includingVariantSubPanel;
    }


    protected JLabel generateTitle(String textElement) {
        JLabel titleLabel = new JLabel(Resource.getTextElement(textElement), SwingConstants.LEFT);
        titleLabel.setFont(Resource.TITLE_FONT);
        titleLabel.setForeground(Resource.TITLE_COLOR);
        return titleLabel;
    }

}
