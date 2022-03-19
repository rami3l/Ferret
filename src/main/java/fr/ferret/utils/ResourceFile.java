package fr.ferret.utils;

import com.pivovarit.function.ThrowingFunction;
import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.settings.HumanGenomeVersions;
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
     * Get a resource
     *
     * @param filename The resource filename
     * @param extractionMethod The methode to use to extract the resource
     * @param <T> The type of the extracted resource
     * @return The extracted resource
     */
    public <T> Optional<T> getResource(String filename,
            ThrowingFunction<URL, T, Exception> extractionMethod) {
        T resource = null;
        try {
            // we try to read the resource from the resource file
            resource = extractionMethod.apply(ResourceFile.class.getResource(filename));
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Failed to get resource file %s", filename), e);
        }
        return Optional.ofNullable(resource);
    }

    public InputStreamReader getFileReader(String file) {
        return new InputStreamReader(ResourceFile.class.getClassLoader().getResourceAsStream(file));
    }

    public <T> Optional<T> readResource(String file,
        ThrowingFunction<BufferedReader, T, Exception> extractionMethod) {
        T resource = null;
        try(var streamReader = getFileReader(file);
                var reader = new BufferedReader(streamReader)) {
            resource = extractionMethod.apply(reader);
        } catch (Exception e) {
            ExceptionHandler.ressourceAccessError(e);
        }
        return Optional.ofNullable(resource);
    }

    /**
     * Gets the file of chromosome ending positions for the selected hgVersion
     *
     * @param hgVersion the human genome version
     * @return the file of ending positions
     */
    public InputStreamReader getChrEndPositionsFile(HumanGenomeVersions hgVersion) {
        return getFileReader("chrEndPositions/" + hgVersion + ".txt");
    }

}
