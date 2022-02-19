package fr.ferret.model.utils;

import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass
public class FileReader {

    private final Map<String, String> delimiters = Map.of("csv", ",", "tab", "\t", "tsv", "\t", "txt", " ");

    public Optional<String> getCsvDelimiter(String filename) {
        return Optional.ofNullable(filename)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(f.lastIndexOf(".") + 1))
            .map(delimiters::get);
    }

    public List<String> readCsvLike(String filename, String invalidRegex) throws IOException {
        var optionalDelemiter = getCsvDelimiter(filename);

        List<String> content = new ArrayList<>();
        if (optionalDelemiter.isEmpty()) {
            // If the delimiter could not be deduced from the file extension
            throw new FileFormatException();
        } else {
            String delimiter = optionalDelemiter.get();
            try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filename))) {

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
