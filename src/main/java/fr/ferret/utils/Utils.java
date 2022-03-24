package fr.ferret.utils;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;

@UtilityClass
public class Utils {
    public boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public String removeExtensionIfInList(String filename, List<String> knownExtensions) {
        var ext = Utils.getExtension(filename);
        // If the file has an extension and this extension is one of the FileOutputType.Extension
        if(ext.isPresent() && knownExtensions.contains(ext.get())) {
            return filename.substring(0, filename.lastIndexOf("."));
        } else {
            return filename;
        }
    }
}
