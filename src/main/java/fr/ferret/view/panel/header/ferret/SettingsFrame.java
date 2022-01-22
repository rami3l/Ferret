package fr.ferret.view.panel.header.ferret;

import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import fr.ferret.controller.settings.FerretConfig;
import fr.ferret.controller.settings.HumanGenomeVersions;
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.controller.settings.SettingsFrameController;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import lombok.Getter;

/**
 * The ferret settings frame
 */
public class SettingsFrame extends JFrame {

    private static final String FONT = "SansSerif";

    @Getter
    private final FerretConfig config;


    private JPanel settingsPanel;

    private JRadioButton allFilesButton;
    private JRadioButton freqFileButton;
    private JRadioButton vcfFileButton;
    private JRadioButton[] phaseButtons;

    private JFormattedTextField mafText;

    private JRadioButton[] humanVersionButtons;

    private JButton settingsOK;
    private JButton settingsCancel;



    public SettingsFrame(FerretFrame ferretFrame, FerretConfig config) {

        super(Resource.getTextElement("settings.title"));
        this.config = config;

        // Initialize the settings panel
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        /* --- Phase selection --- */
        generatePhaseSelectionSection();

        /* --- MAF section --- */
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        generateMAF();

        // Code améliorable
        // TODO PGROU : une barre pour le min, une barre pour le max

        /* --- Files output type --- */
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        generateOuputFilesSection();

        /* --- Human genome versions --- */
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        generateHgVersionButtons();

        /* --- Ok/Cancel button --- */
        generateOkCancelButtons();

        // Sets the settings windows
        this.getContentPane().add(settingsPanel);
        this.setResizable(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();

        // TODO: should these addActionListener be in the view part ?
        settingsCancel.addActionListener(new SettingsFrameController.CancelButtonListener(this));
        settingsOK.addActionListener(new SettingsFrameController.SaveButtonListener(ferretFrame,
                this, phaseButtons, humanVersionButtons, mafText, allFilesButton, freqFileButton,
                vcfFileButton));
    }

    /**
     * Generates the phase selection section
     */
    private void generatePhaseSelectionSection() {

        JLabel vcfVersionLabel = new JLabel(Resource.getTextElement("settings.genversion"));
        vcfVersionLabel.setFont(new Font(FONT, Font.BOLD, 16));
        settingsPanel.add(vcfVersionLabel);

        // list of all phase buttons
        phaseButtons = new JRadioButton[Phases1KG.values().length];

        // phase buttons are grouped (in order to let only one of them to be selected)
        ButtonGroup vcfRadioButtons = new ButtonGroup();

        // we add buttons to the list and the group, and set their text
        for (int i = 0; i < phaseButtons.length; i++) {
            phaseButtons[i] = new JRadioButton(
                    Resource.getTextElement("settings.phase." + Phases1KG.values()[i].name()));
            vcfRadioButtons.add(phaseButtons[i]);
            settingsPanel.add(phaseButtons[i]);
        }

        // Default button
        phaseButtons[config.getSelectedVersion().ordinal()].setSelected(true);

        // Disable NYGC : not implemented
        phaseButtons[Phases1KG.NYGC_30X.ordinal()].setEnabled(false);

    }

    /**
     * Generates the MAF section
     */
    private void generateMAF() {

        Optional<ImageIcon> questionMark = Resource.getIcon("/img/questionMark25.png");
        JLabel questionMarkMAFThreshold =
                questionMark.isPresent() ? new JLabel(questionMark.get()) : null;

        JSlider mafSlider = new JSlider(0, 5000, 0);
        JLabel mafThresholdLabel = new JLabel(Resource.getTextElement("settings.mafthresold"));
        JLabel mafOptionLabel = new JLabel(Resource.getTextElement("settings.maf"));
        mafOptionLabel.setFont(new Font(FONT, Font.BOLD, 16));

        JPanel mafPanel = new JPanel();
        mafPanel.setLayout(new BoxLayout(mafPanel, BoxLayout.X_AXIS));

        JPanel mafESPPanel = new JPanel();

        // TODO bonus : GnomAD si le temps

        NumberFormat mafFormat = NumberFormat.getNumberInstance();
        mafFormat.setMaximumFractionDigits(4);
        mafText = new JFormattedTextField(mafFormat);

        mafPanel.setAlignmentX(LEFT_ALIGNMENT);
        mafPanel.add(mafThresholdLabel);
        mafText.setColumns(5);
        mafText.setMaximumSize(mafText.getPreferredSize());
        mafPanel.add(mafText);

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel("0.0"));
        labels.put(5000, new JLabel("0.5"));
        mafSlider.setLabelTable(labels);
        mafSlider.setValue(0);
        mafSlider.setPaintLabels(true);
        mafPanel.add(mafSlider);

        SettingsFrameController.MafInputListener mafController =
                new SettingsFrameController.MafInputListener(mafText, mafSlider);
        mafText.addPropertyChangeListener(mafController);
        mafSlider.addChangeListener(mafController);
        mafText.setValue(config.getMafThreshold());

        if (questionMarkMAFThreshold != null) {
            mafPanel.add(questionMarkMAFThreshold);
            questionMarkMAFThreshold.setToolTipText(Resource.getTextElement("settings.maf.help"));
        }
        mafPanel.add(Box.createHorizontalGlue());
        mafESPPanel.setLayout(new BoxLayout(mafESPPanel, BoxLayout.X_AXIS));
        mafESPPanel.setAlignmentX(LEFT_ALIGNMENT);

        settingsPanel.add(mafOptionLabel);
        settingsPanel.add(mafPanel);
        settingsPanel.add(mafESPPanel);

    }

    /**
     * Generate the output files type selection section
     */
    private void generateOuputFilesSection() {

        // Section title
        JLabel filesLabel = new JLabel(Resource.getTextElement("settings.outfiles"));
        filesLabel.setFont(new Font(FONT, Font.BOLD, 16));
        settingsPanel.add(filesLabel);

        // 3 buttons to select output files possible type
        allFilesButton = new JRadioButton(Resource.getTextElement("settings.out.frqmap"));
        freqFileButton = new JRadioButton(Resource.getTextElement("settings.out.frq"));
        vcfFileButton = new JRadioButton(Resource.getTextElement("settings.out.vcf"));

        // group buttons (to let only one to be selected)
        ButtonGroup fileOutputButtons = new ButtonGroup();
        fileOutputButtons.add(allFilesButton);
        fileOutputButtons.add(freqFileButton);
        fileOutputButtons.add(vcfFileButton);

        // selects the button corresponding to actual settings
        switch (config.getSelectedOutputType()) {
            case ALL -> allFilesButton.setSelected(true);
            case FRQ -> freqFileButton.setSelected(true);
            case VCF -> vcfFileButton.setSelected(true);
        }

        // Adds the 3 buttons to the panel
        settingsPanel.add(allFilesButton);
        settingsPanel.add(freqFileButton);
        settingsPanel.add(vcfFileButton);
    }

    /**
     * Generates the Human Genome version selection
     */
    private void generateHgVersionButtons() {

        // Section title
        JLabel hgVersionLabel = new JLabel(Resource.getTextElement("settings.hugversion"));
        hgVersionLabel.setFont(new Font(FONT, Font.BOLD, 16));
        settingsPanel.add(hgVersionLabel);

        // List of radio buttons (one by possible human genome version)
        humanVersionButtons = new JRadioButton[HumanGenomeVersions.values().length];
        ButtonGroup hgVersionButtons = new ButtonGroup();
        for (int i = 0; i < humanVersionButtons.length; i++) {
            humanVersionButtons[i] = new JRadioButton(Resource
                    .getTextElement("settings.hugen." + HumanGenomeVersions.values()[i].name()));
            hgVersionButtons.add(humanVersionButtons[i]);
            settingsPanel.add(humanVersionButtons[i]);
        }
        // Selects the button corresponding to actual settings
        humanVersionButtons[config.getSelectedHumanGenome().ordinal()].setSelected(true); // Default
    }

    /**
     * Generates the Ok and Cancel buttons sectoin
     */
    private void generateOkCancelButtons() {

        // Subpanel containing the buttons
        JPanel settingsButtonPanel = new JPanel();
        settingsButtonPanel.setAlignmentX(LEFT_ALIGNMENT);
        settingsButtonPanel.setLayout(new BoxLayout(settingsButtonPanel, BoxLayout.X_AXIS));
        settingsButtonPanel.add(Box.createHorizontalGlue());

        // Creates and add the buttons to the subpanel
        settingsOK = new JButton(Resource.getTextElement("settings.ok"));
        settingsCancel = new JButton(Resource.getTextElement("settings.cancel"));
        settingsButtonPanel.add(settingsCancel);
        settingsButtonPanel.add(settingsOK);

        // Adds the subpanel to the settings panel
        settingsPanel.add(settingsButtonPanel);
    }

}
