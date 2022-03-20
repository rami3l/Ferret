package fr.ferret.utils;

import com.pivovarit.function.ThrowingFunction;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@UtilityClass
class ResourceFile {

    private final Logger logger = Logger.getLogger(ResourceFile.class.getName());

    /**
     * Gets a resource
     *
     * @param file The resource filename
     * @param extractionMethod The method to use to extract the resource. It must take a URL.
     * @see #readResource
     * @param <T> The type of the extracted resource
     * @return The extracted resource
     */
    public <T> Optional<T> getResource(String file,
            ThrowingFunction<URL, T, Exception> extractionMethod) {
        T resource = null;
        try {
            // we try to read the resource from the resource file
            resource = extractionMethod.apply(ResourceFile.class.getResource(file));
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Failed to get resource file %s", file), e);
        }
        return Optional.ofNullable(resource);
    }

    public Optional<InputStreamReader> getFileReader(String file) {
        return Optional.ofNullable(ResourceFile.class.getClassLoader().getResourceAsStream(file))
            .map(InputStreamReader::new);
    }

    /**
     * This method is similar to {@link ResourceFile#getResource} but used for extraction methods
     * which need a {@link BufferedReader} instead of a resource {@link URL}
     */
    public <T> Optional<T> readResource(String file,
        ThrowingFunction<BufferedReader, T, Exception> extractionMethod) {
        T resource = null;
        try(var streamReader = getFileReader(file).get();
                var reader = new BufferedReader(streamReader)) {
            resource = extractionMethod.apply(reader);
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Failed to get resource file %s", file), e);
        }
        return Optional.ofNullable(resource);
    }

}
