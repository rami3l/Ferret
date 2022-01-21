package fr.ferret.view.panel;

import static fr.ferret.view.utils.GuiUtils.addToPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import fr.ferret.controller.BrowseFileButtonListener;
import fr.ferret.utils.Resource;
import lombok.Getter;

/**
 * The gene panel of Ferret <br>
 * Selection of the genes parameters
 */
@Getter
public class GenePanel extends JPanel {

    private JTextField inputField;
    private BrowseFileButtonListener fileSelector;
    private JRadioButton rdoName;
    private JRadioButton rdoID;

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
        setBorder(BorderFactory.createLineBorder(new Color(131, 55, 192, 140), 4));
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
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 24));
        titleLabel.setForeground(new Color(18, 0, 127));
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

        // Choice subpanel (Name / ID)
        JPanel choice = generateChoicePanel();

        // Or Label
        JLabel labOr = new JLabel(Resource.getTextElement("gene.or"));
        labOr.setBorder(BorderFactory.createEmptyBorder(0, 130, 0, 10));
        labOr.setFont(new Font(labOr.getFont().getFontName(), Font.PLAIN, 16));

        // File selection
        JButton browseButton = new JButton(Resource.getTextElement("gene.browse"));
        browseButton.setPreferredSize(new Dimension(200, 30));
        browseButton.setBackground(new Color(201, 157, 240));

        JLabel selectedFile = new JLabel(Resource.getTextElement("gene.selectfile"));
        selectedFile.setFont(new Font(selectedFile.getFont().getFontName(), Font.PLAIN, 13));

        fileSelector = new BrowseFileButtonListener(this, browseButton, selectedFile);

        // Add the elements defined above to the input panel
        addToPanel(inputPanel, inputField, 0.8, 1, 1);
        addToPanel(inputPanel, labOr, 0.3, 2, 1);
        addToPanel(inputPanel, browseButton, 0.3, 3, 1);
        addToPanel(inputPanel, selectedFile, 0.3, 3, 2);
        addToPanel(inputPanel, choice, 0.8, 1, 2);

        return inputPanel;
    }

    /**
     * Generates the choice part (checboxes) of the input panel
     * 
     * @return the choice part subpanel
     */
    private JPanel generateChoicePanel() {

        JPanel choice = new JPanel(new GridBagLayout());

        // Name radio button
        rdoName = new JRadioButton(Resource.getTextElement("gene.name"), true);
        rdoName.setFont(new Font(rdoName.getFont().getFontName(), Font.PLAIN, 16));

        // ID radio button
        rdoID = new JRadioButton(Resource.getTextElement("gene.ID"));
        rdoID.setFont(new Font(rdoID.getFont().getFontName(), Font.PLAIN, 16));

        // We group the to buttons (to let only one of them to be selected)
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rdoName);
        buttonGroup.add(rdoID);

        // Add the elements defined above to the input panel
        addToPanel(choice, rdoName, 0.5, 1, 1);
        addToPanel(choice, rdoID, 0.5, 2, 1);

        return choice;
    }

}
