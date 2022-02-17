package fr.ferret.controller.utils;

import fr.ferret.utils.Resource;
import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.io.File;
import java.util.Optional;

@UtilityClass
public class FileUtils {

    public Optional<File> chooseFile(JPanel panel, int mode) {
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.setFileSelectionMode(mode);
        saveFileChooser.setDialogTitle(Resource.getTextElement("run.save"));
        int returnVal = saveFileChooser.showSaveDialog(panel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return Optional.of(saveFileChooser.getSelectedFile());
        }
        return Optional.empty();
    }

}
