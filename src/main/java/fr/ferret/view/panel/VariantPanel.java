package fr.ferret.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import fr.ferret.FerretMain;
import fr.ferret.controller.BrowseFileButtonListener;

/**
 * The variant panel of Ferret <br>
 * Selection of the gene variants parameters
 */
public class VariantPanel extends JPanel {
    // private final JComboBox<String> chromosomeList;
    private final JTextField variantIdField;
    private final JCheckBox checkbox;
    private final JTextField bpField;
    private final BrowseFileButtonListener fileSelector;

    public VariantPanel() {
        // Labels
        JLabel titleLabel =
                new JLabel(FerretMain.getLocale().getString("variant.input"), SwingConstants.LEFT);
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 24));
        titleLabel.setForeground(new Color(18, 0, 127));

        JLabel helpLabel1 =
                new JLabel(FerretMain.getLocale().getString("variant.help"), SwingConstants.CENTER);

        // Input panel

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());

        // JLabel lab_inputnameorid = new JLabel(FerretTest.locale.getString("gene.inputnameorid"));
        JLabel labOr = new JLabel(FerretMain.getLocale().getString("variant.or"));
        labOr.setBorder(BorderFactory.createEmptyBorder(0, 130, 0, 10));
        labOr.setFont(new Font(labOr.getFont().getFontName(), Font.PLAIN, 16));

        variantIdField = new JTextField();
        bpField = new JTextField();

        JLabel selectedFile = new JLabel(FerretMain.getLocale().getString("variant.selectfile"));
        JButton browseButton = new JButton(FerretMain.getLocale().getString("variant.browse"));
        browseButton.setPreferredSize(new Dimension(200, 30));
        browseButton.setBackground(new Color(201, 157, 240));
        fileSelector = new BrowseFileButtonListener(this, browseButton, selectedFile);
        // RunButtonListener listener = new RunButtonListener(frame, browseButton);

        checkbox = new JCheckBox(FerretMain.getLocale().getString("variant.bpcheckbox"));

        JLabel bp = new JLabel(FerretMain.getLocale().getString("variant.bp"));

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.8;
        c.gridx = 1;
        c.gridy = 1;
        inputPanel.add(variantIdField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.2;
        c.gridx = 2;
        c.gridy = 1;
        inputPanel.add(labOr, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.6;
        c.gridx = 3;
        c.gridy = 1;
        inputPanel.add(browseButton, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.6;
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
        c.weightx = 0.02;
        c.gridx = 1;
        c.gridy = 1;
        checkbox.setFont(new Font(checkbox.getFont().getFontName(), Font.PLAIN, 13));
        choice.add(checkbox, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.4;
        c.gridx = 2;
        c.gridy = 1;
        choice.add(bpField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.3;
        c.gridx = 3;
        c.gridy = 1;
        bp.setFont(new Font(bp.getFont().getFontName(), Font.PLAIN, 13));
        choice.add(bp, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.7;
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

    public JTextField getVariantIdField() {
        return variantIdField;
    }

    public BrowseFileButtonListener getFileSelector() {
        return fileSelector;
    }

    public JCheckBox getCheckbox() {
        return checkbox;
    }

    public JTextField getBpField() {
        return bpField;
    }
}
