package fr.ferret.model.utils;

import com.google.common.io.Files;
import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass
public class FileReader {

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
        var optionalDelemiter = getDelimiter(filename);

        List<String> content = new ArrayList<>();
        if (optionalDelemiter.isEmpty()) {
            // If the delimiter could not be deduced from the file extension
            throw new FileFormatException();
        } else {
            String delimiter = optionalDelemiter.get();
            try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filename));) {

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
