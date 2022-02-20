package fr.ferret.utils;

import lombok.experimental.UtilityClass;

import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@UtilityClass
public class ResourceFile {

    private final Logger logger = Logger.getLogger(ResourceFile.class.getName());


    public <T> Optional<T> getResource(String filename, ThrowingFunction<URL, T> extractionMethod) {
        T resource = null;
        try {
            // we try to read the resource from the resource file
            resource = extractionMethod.apply(ResourceFile.class.getResource(filename));
        } catch (Exception e) {
            logger.log(Level.WARNING,
                String.format("Failed to get resource file %s", filename), e);
        }
        return Optional.ofNullable(resource);
    }
}
