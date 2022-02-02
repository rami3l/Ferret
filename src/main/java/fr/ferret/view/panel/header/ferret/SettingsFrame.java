package fr.ferret.view.panel.header.ferret;

import java.awt.Dimension;
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

    @Getter
    private final FerretConfig config;


    private JPanel settingsPanel;

    private JRadioButton allFilesButton;
    private JRadioButton freqFileButton;
    private JRadioButton vcfFileButton;
    private JRadioButton[] phaseButtons;

    private JFormattedTextField mafValueField;

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

        // Sets the settings window
        this.getContentPane().add(settingsPanel);
        this.setResizable(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();

        // TODO: should these addActionListener be in the view part ?
        settingsCancel.addActionListener(new SettingsFrameController.CancelButtonListener(this));
        settingsOK.addActionListener(new SettingsFrameController.SaveButtonListener(ferretFrame,
                this, phaseButtons, humanVersionButtons, mafValueField, allFilesButton,
                freqFileButton, vcfFileButton));
    }

    /**
     * Generates the phase selection section
     */
    private void generatePhaseSelectionSection() {

        JLabel vcfVersionLabel = new JLabel(Resource.getTextElement("settings.genversion"));
        vcfVersionLabel.setFont(Resource.SETTINGS_LABEL_FONT);
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

        /* --- Section title --- */
        JLabel mafOptionLabel = new JLabel(Resource.getTextElement("settings.maf"));
        mafOptionLabel.setFont(Resource.SETTINGS_LABEL_FONT);


        /* --- Value selection panel: divided in two parts --- */
        JPanel mafPanel = new JPanel();
        mafPanel.setLayout(new BoxLayout(mafPanel, BoxLayout.X_AXIS));
        mafPanel.setAlignmentX(LEFT_ALIGNMENT);

        // -- First part: value selection field --
        // Label
        JLabel mafThresholdLabel = new JLabel(Resource.getTextElement("settings.mafthresold"));

        // Field
        NumberFormat mafFormat = NumberFormat.getNumberInstance();
        mafFormat.setMaximumFractionDigits(4);
        mafValueField = new JFormattedTextField(mafFormat);
        mafValueField.setColumns(5);
        mafValueField.setMaximumSize(mafValueField.getPreferredSize());

        // we set the value to the actual setting
        mafValueField.setValue(config.getMafThreshold());

        // Label and field added to the panel
        mafPanel.add(mafThresholdLabel);
        mafPanel.add(mafValueField);

        // -- Second part: value selection slider --
        JSlider mafSlider = new JSlider(0, 5000, 0);

        // Slider labels
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel("0.0"));
        labels.put(5000, new JLabel("0.5"));
        mafSlider.setLabelTable(labels);
        mafSlider.setPaintLabels(true);

        // Value initialized to 0
        mafSlider.setValue(0);

        // slide added to the panel
        mafPanel.add(mafSlider);


        /* --- Help section --- */
        JPanel mafHelpPanel = new JPanel();
        mafHelpPanel.setLayout(new BoxLayout(mafHelpPanel, BoxLayout.X_AXIS));
        mafHelpPanel.setAlignmentX(LEFT_ALIGNMENT);

        // We try to get the question mark icon
        Optional<ImageIcon> questionMark = Resource.getIcon("/img/questionMark25.png");
        if (questionMark.isPresent()) {
            // if icon got sucessfully we add an help tooltip
            JLabel questionMarkMAFThreshold = new JLabel(questionMark.get());
            questionMarkMAFThreshold.setToolTipText(Resource.getTextElement("settings.maf.help"));
            // and we add it to the panel
            mafPanel.add(questionMarkMAFThreshold);
        }

        mafPanel.add(Box.createHorizontalGlue());
        // TODO bonus : GnomAD si le temps

        // We add the three parts to the settings panel
        settingsPanel.add(mafOptionLabel);
        settingsPanel.add(mafPanel);
        settingsPanel.add(mafHelpPanel);

        // We link together field and slider values
        SettingsFrameController.MafInputListener mafController =
                new SettingsFrameController.MafInputListener(mafValueField, mafSlider);
        mafValueField.addPropertyChangeListener(mafController);
        mafSlider.addChangeListener(mafController);
    }

    /**
     * Generate the output files type selection section
     */
    private void generateOuputFilesSection() {

        // Section title
        JLabel filesLabel = new JLabel(Resource.getTextElement("settings.outfiles"));
        filesLabel.setFont(Resource.SETTINGS_LABEL_FONT);
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
        hgVersionLabel.setFont(Resource.SETTINGS_LABEL_FONT);
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
