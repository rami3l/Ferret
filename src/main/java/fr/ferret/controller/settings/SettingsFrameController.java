package fr.ferret.controller.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFormattedTextField;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.header.ferret.SettingsFrame;
import lombok.AllArgsConstructor;

/**
 * Groups all controllers for the {@link SettingsFrame}
 */
public class SettingsFrameController {
    /**
     * Cancel button click listener
     */
    @AllArgsConstructor
    public static class CancelButtonListener implements ActionListener {

        private final SettingsFrame settingsFrame;

        @Override
        public void actionPerformed(ActionEvent e) {
            settingsFrame.dispose();
        }
    }

    /**
     * Save button click listener
     */
    @AllArgsConstructor
    public static class SaveButtonListener implements ActionListener {

        private final FerretFrame ferretFrame;
        private final SettingsFrame settingsFrame;
        private final JRadioButton[] phaseButtons;
        private final JRadioButton[] humanVersionButtons;
        private final JFormattedTextField mafText;
        private final JRadioButton allFilesButton;
        private final JRadioButton freqFileButton;
        private final JRadioButton vcfFileButton;

        @Override
        public void actionPerformed(ActionEvent e) {
            Phases1KG selected = null;
            for (int i = 0; i < phaseButtons.length; i++) {
                JRadioButton button = phaseButtons[i];
                if (button.isSelected()) {
                    selected = Phases1KG.values()[i];
                    break;
                }
            }
            if (selected == null) {
                throw new IllegalStateException("No phases selected");
            }
            settingsFrame.getConfig().setSelectedVersion(selected);

            settingsFrame.getConfig().setMafThreshold(((Number) mafText.getValue()).doubleValue());
            if (allFilesButton.isSelected()) {
                settingsFrame.getConfig().setSelectedOutputType(FileOutputType.ALL);
            } else if (freqFileButton.isSelected()) {
                settingsFrame.getConfig().setSelectedOutputType(FileOutputType.FRQ);
            } else if (vcfFileButton.isSelected()) {
                settingsFrame.getConfig().setSelectedOutputType(FileOutputType.VCF);
            }

            HumanGenomeVersions selectedv = null;
            for (int i = 0; i < humanVersionButtons.length; i++) {
                JRadioButton button = humanVersionButtons[i];
                if (button.isSelected()) {
                    selectedv = HumanGenomeVersions.values()[i];
                    break;
                }
            }
            if (selectedv == null) {
                throw new IllegalStateException("No human gene version selected");
            }
            settingsFrame.getConfig().setSelectedHumanGenome(selectedv);

            ferretFrame.getLocusPanel().getTitleLabel().setText(Resource.getTextElement(
                    "locus.input." + Resource.CONFIG.getSelectedHumanGenome().name()));
            ferretFrame.getRegionPanel().reloadPanel();
            settingsFrame.dispose();
        }
    }

    /**
     * Maf inputs listener
     */
    @AllArgsConstructor
    public static class MafInputListener implements PropertyChangeListener, ChangeListener {
        private final JFormattedTextField mafText;
        private final JSlider mafSlider;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // Maf text input change
            double localMAFThreshold = ((Number) mafText.getValue()).doubleValue();
            if (localMAFThreshold > 0.5) {
                localMAFThreshold = 0.5;
                mafText.setValue(localMAFThreshold);
            } else if (localMAFThreshold < 0.0) {
                localMAFThreshold = 0.0;
                mafText.setValue(localMAFThreshold);
            }
            mafSlider.setValue((int) (localMAFThreshold * 10000));
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            // Maf slider change
            double localMAFThreshold = mafSlider.getValue();
            mafText.setValue(localMAFThreshold / 10000);
        }
    }
}
