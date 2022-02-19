package fr.ferret.controller.utils;

import com.google.common.io.Files;
import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import fr.ferret.utils.Resource;
import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass public class FileUtils {

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

    public Optional<String> getDelimiter(String filename) {
        String extension = Files.getFileExtension(filename);
        return Optional.ofNullable(switch (extension) {
            case "csv" -> ",";
            case "tab", "tsv" -> "\\t";
            case "txt" -> " ";
            default -> null;
        });
    }


    public List<String> readCsvLike(String filename, String invalidRegex) throws IOException {
        var optionalDelemiter = FileUtils.getDelimiter(filename);

        List<String> content = new ArrayList<>();
        if (optionalDelemiter.isEmpty()) {
            // If the delimiter could not be deduced from the file extension
            throw new FileFormatException();
        } else {
            String delimiter = optionalDelemiter.get();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename));) {

                reader.lines().flatMap(line -> Stream.of(line.split(delimiter))).map(String::trim)
                    .forEach(text -> {
                        if (text.matches(invalidRegex))
                            throw new RuntimeException();
                        content.add(text);
                    });
            } catch (Exception e) {
                throw new FileContentException();
            }
        }
        return content;
    }

}
