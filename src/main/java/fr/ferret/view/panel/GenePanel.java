package fr.ferret.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import fr.ferret.FerretMain;
import fr.ferret.controller.BrowseFileButtonListener;
import lombok.Getter;

/**
 * The gene panel of Ferret <br>
 * Selection of the genes parameters
 */
@Getter
public class GenePanel extends JPanel {

    private final JTextField inputField;
    private final BrowseFileButtonListener fileSelector;
    private final JRadioButton rdoName;
    private final JRadioButton rdoID;

    public GenePanel() {
        // Labels
        JLabel titleLabel =
                new JLabel(FerretMain.getLocale().getString("gene.input"), SwingConstants.LEFT);
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 24));
        titleLabel.setForeground(new Color(18, 0, 127));

        JLabel helpLabel1 =
                new JLabel(FerretMain.getLocale().getString("gene.help"), SwingConstants.CENTER);

        // Input panel

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());

        // JLabel lab_inputnameorid = new JLabel(FerretTest.locale.getString("gene.inputnameorid"));
        JLabel labOr = new JLabel(FerretMain.getLocale().getString("gene.or"));
        labOr.setBorder(BorderFactory.createEmptyBorder(0, 130, 0, 10));
        labOr.setFont(new Font(labOr.getFont().getFontName(), Font.PLAIN, 16));

        inputField = new JTextField();

        JLabel selectedFile = new JLabel(FerretMain.getLocale().getString("gene.selectfile"));
        JButton browseButton = new JButton(FerretMain.getLocale().getString("gene.browse"));
        browseButton.setPreferredSize(new Dimension(200, 30));
        browseButton.setBackground(new Color(201, 157, 240));
        fileSelector = new BrowseFileButtonListener(this, browseButton, selectedFile);
        // RunButtonListener listener = new RunButtonListener(frame, browseButton);

        ButtonGroup buttonGroup = new ButtonGroup();
        rdoName = new JRadioButton(FerretMain.getLocale().getString("gene.name"));
        rdoID = new JRadioButton(FerretMain.getLocale().getString("gene.ID"));

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.8;
        c.gridx = 1;
        c.gridy = 1;
        inputPanel.add(inputField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.3;
        c.gridx = 2;
        c.gridy = 1;
        inputPanel.add(labOr, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.3;
        c.gridx = 3;
        c.gridy = 1;
        inputPanel.add(browseButton, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.3;
        c.gridx = 3;
        c.gridy = 2;
        selectedFile.setFont(new Font(selectedFile.getFont().getFontName(), Font.PLAIN, 13));
        inputPanel.add(selectedFile, c);

        /*
         * c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 0.3; c.gridx = 1; c.gridy = 2;
         * lab_inputnameorid.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
         * lab_inputnameorid.setFont(new Font(lab_inputnameorid.getFont().getFontName(), Font.PLAIN,
         * 16)); inputPanel.add(lab_inputnameorid, c);
         */

        JPanel choice = new JPanel();
        choice.setLayout(new GridBagLayout());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        rdoName.setSelected(true);
        rdoName.setFont(new Font(rdoName.getFont().getFontName(), Font.PLAIN, 16));
        choice.add(rdoName, c);
        buttonGroup.add(rdoName);
        // rdoName.addItemListener( this::radioButtons_itemStateChanged );

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 1;
        rdoID.setFont(new Font(rdoID.getFont().getFontName(), Font.PLAIN, 16));
        choice.add(rdoID, c);
        buttonGroup.add(rdoID);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.8;
        c.gridx = 1;
        c.gridy = 2;
        inputPanel.add(choice, c);
        // rdoGreen.addItemListener( this::radioButtons_itemStateChanged );

        // Add elements

        this.setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(helpLabel1, BorderLayout.SOUTH);

        // Borders
        setBorder(BorderFactory.createLineBorder(new Color(131, 55, 192, 140), 4));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        helpLabel1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    }

}
