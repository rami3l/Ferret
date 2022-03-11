package fr.ferret.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.pivovarit.function.ThrowingFunction;
import fr.ferret.controller.settings.HumanGenomeVersions;
import fr.ferret.controller.settings.Phases1KG;
import lombok.experimental.UtilityClass;

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

    public InputStream getFileInputStream(String file) {
        return Resource.class.getClassLoader().getResourceAsStream(file);
    }

    /**
     * Gets the file of population samples (people ids by regions and zones)
     *
     * @param phase the phase to get samples from
     * @return the file of population samples
     */
    public InputStream getSampleFile(Phases1KG phase) {
        return getFileInputStream("samples/" + phase + ".txt");
    }

    /**
     * Gets the file of chromosome ending positions for the selected hgVersion
     *
     * @param hgVersion the human genome version
     * @return the file of ending positions
     */
    public InputStream getChrEndPositionsFile(HumanGenomeVersions hgVersion) {
        return getFileInputStream("chrEndPositions/" + hgVersion + ".txt");
    }

}
