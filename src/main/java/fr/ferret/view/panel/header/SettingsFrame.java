package fr.ferret.view.panel.header;

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
import fr.ferret.view.FerretFrame;
import fr.ferret.view.utils.Resource;
import lombok.Getter;

/**
 * The ferret settings frame
 */
public class SettingsFrame extends JFrame {

    private static final String FONT = "SansSerif";

    @Getter
    private final FerretConfig config;

    public SettingsFrame(FerretFrame ferretFrame, FerretConfig config) {
        super(Resource.getTextElement("settings.title"));
        this.config = config;

        Optional<ImageIcon> questionMark = Resource.getIcon("/img/questionMark25.png");
        JLabel questionMarkMAFThreshold =
                questionMark.isPresent() ? new JLabel(questionMark.get()) : null;

        JPanel settingsPanel = new JPanel();
        this.getContentPane().add(settingsPanel);

        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setResizable(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JRadioButton[] phaseButtons = new JRadioButton[Phases1KG.values().length];
        JRadioButton[] humanVersionButtons = new JRadioButton[HumanGenomeVersions.values().length];

        // Phases
        {
            JLabel vcfVersionLabel = new JLabel(Resource.getTextElement("settings.genversion"));
            vcfVersionLabel.setFont(new Font(FONT, Font.BOLD, 16));
            settingsPanel.add(vcfVersionLabel);

            ButtonGroup vcfRadioButtons = new ButtonGroup();
            for (int i = 0; i < phaseButtons.length; i++) {
                phaseButtons[i] = new JRadioButton(
                        Resource.getTextElement("settings.phase." + Phases1KG.values()[i].name()));
                vcfRadioButtons.add(phaseButtons[i]);
                settingsPanel.add(phaseButtons[i]);
            }
            phaseButtons[config.getSelectedVersion().ordinal()].setSelected(true); // Default button
            phaseButtons[Phases1KG.NYGC_30X.ordinal()].setEnabled(false); // Disable NYGC : not
                                                                          // implemented
        }

        // MAF
        // Code améliorable
        // TODO PGROU : une barre pour le min, une barre pour le max

        JSlider mafSlider = new JSlider(0, 5000, 0);
        JLabel nafThresholdLabel = new JLabel(Resource.getTextElement("settings.mafthresold"));
        JLabel nafOptionLabel = new JLabel(Resource.getTextElement("settings.maf"));

        JPanel mafPanel = new JPanel();
        JPanel mafESPPanel = new JPanel();

        // JCheckBox ESPMAF = new JCheckBox("Apply MAF threshold to the Exome Sequencing Project");
        // TODO bonus : GnomAD si le temps

        NumberFormat mafFormat = NumberFormat.getNumberInstance();
        mafFormat.setMaximumFractionDigits(4);
        final JFormattedTextField mafText = new JFormattedTextField(mafFormat);
        {
            settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mafPanel.setAlignmentX(LEFT_ALIGNMENT);
            settingsPanel.add(nafOptionLabel);
            nafOptionLabel.setFont(new Font(FONT, Font.BOLD, 16));
            settingsPanel.add(mafPanel);
            mafPanel.setLayout(new BoxLayout(mafPanel, BoxLayout.X_AXIS));
            mafPanel.add(nafThresholdLabel);
            mafText.setColumns(5);
            mafText.setMaximumSize(mafText.getPreferredSize());
            mafPanel.add(mafText);

            mafSlider.setMajorTickSpacing(1000);
            mafSlider.setPaintTicks(true);
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
                questionMarkMAFThreshold
                        .setToolTipText(Resource.getTextElement("settings.maf.help"));
            }
            mafPanel.add(Box.createHorizontalGlue());
            mafESPPanel.setLayout(new BoxLayout(mafESPPanel, BoxLayout.X_AXIS));
            mafESPPanel.setAlignmentX(LEFT_ALIGNMENT);
            // mafESPPanel.add(ESPMAF);
            settingsPanel.add(mafESPPanel);
        }

        // File output type
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JLabel filesLabel = new JLabel(Resource.getTextElement("settings.outfiles"));
        settingsPanel.add(filesLabel);
        filesLabel.setFont(new Font(FONT, Font.BOLD, 16));

        ButtonGroup fileOutputButtons = new ButtonGroup();
        JRadioButton allFilesButton =
                new JRadioButton(Resource.getTextElement("settings.out.frqmap"));
        JRadioButton freqFileButton = new JRadioButton(Resource.getTextElement("settings.out.frq"));
        JRadioButton vcfFileButton = new JRadioButton(Resource.getTextElement("settings.out.vcf"));
        {
            fileOutputButtons.add(allFilesButton);
            fileOutputButtons.add(freqFileButton);
            fileOutputButtons.add(vcfFileButton);
            settingsPanel.add(allFilesButton);
            settingsPanel.add(freqFileButton);
            settingsPanel.add(vcfFileButton);
            switch (config.getSelectedOutputType()) {
                case ALL:
                    allFilesButton.setSelected(true);
                    break;
                case FRQ:
                    freqFileButton.setSelected(true);
                    break;
                case VCF:
                    vcfFileButton.setSelected(true);
                    break;
            }
        }

        // Human genome versions
        {
            JLabel hgVersionLabel = new JLabel(Resource.getTextElement("settings.hugversion"));
            settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            settingsPanel.add(hgVersionLabel);
            hgVersionLabel.setFont(new Font(FONT, Font.BOLD, 16));

            ButtonGroup hgVersionButtons = new ButtonGroup();
            for (int i = 0; i < humanVersionButtons.length; i++) {
                humanVersionButtons[i] = new JRadioButton(Resource.getTextElement(
                        "settings.hugen." + HumanGenomeVersions.values()[i].name()));
                hgVersionButtons.add(humanVersionButtons[i]);
                settingsPanel.add(humanVersionButtons[i]);
            }
            humanVersionButtons[config.getSelectedHumanGenome().ordinal()].setSelected(true); // Default
                                                                                              // button
        }

        // Ok/Cancel buttons

        JPanel settingsButtonPanel = new JPanel();
        JButton settingsOK = new JButton(Resource.getTextElement("settings.ok"));
        JButton settingsCancel = new JButton(Resource.getTextElement("settings.cancel"));

        settingsButtonPanel.setAlignmentX(LEFT_ALIGNMENT);
        settingsButtonPanel.setLayout(new BoxLayout(settingsButtonPanel, BoxLayout.X_AXIS));
        settingsPanel.add(settingsButtonPanel);
        settingsButtonPanel.add(Box.createHorizontalGlue());

        settingsCancel.addActionListener(new SettingsFrameController.CancelButtonListener(this));
        settingsButtonPanel.add(settingsCancel);

        settingsOK.addActionListener(new SettingsFrameController.SaveButtonListener(ferretFrame,
                this, phaseButtons, humanVersionButtons, mafText, allFilesButton, freqFileButton,
                vcfFileButton));
        settingsButtonPanel.add(settingsOK);

        this.pack();
    }
}
