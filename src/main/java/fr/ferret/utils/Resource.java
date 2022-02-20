package fr.ferret.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import fr.ferret.controller.settings.FerretConfig;
import fr.ferret.controller.settings.HumanGenomeVersions;
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.model.ZoneSelection;
import lombok.experimental.UtilityClass;

/**
 * Util class to deal with resouce files
 */
@UtilityClass
public class Resource {
    /**
     * program settings
     */
    public final FerretConfig CONFIG = new FerretConfig();

    private final Logger logger = Logger.getLogger(Resource.class.getName());

    /**
     * text elements for the interface
     */
    private final ResourceBundle textElements =
            ResourceBundle.getBundle("ferret", Locale.getDefault());

    /**
     * application configuration
     */
    private final ResourceBundle serverConfig = ResourceBundle.getBundle("server");

    public final Color TITLE_COLOR = new Color(18, 0, 150);
    public final Color ZONE_LABEL_COLOR = new Color(131, 55, 192);
    public final Color PANEL_BORDER_COLOR = new Color(131, 55, 192, 140);
    public final Color BUTTON_COLOR = new Color(201, 157, 240);
    public final Color LINK_STANDARD_COLOR = new Color(40, 100, 255);
    public final Color LINK_HOVER_COLOR = new Color(255, 0, 0);
    public final Color LINK_ACTIVE_COLOR = new Color(128, 0, 128);

    public final Font TITLE_FONT = new Font("Calibri", Font.BOLD, 24);
    public final Font ZONE_LABEL_FONT = new Font("Calibri", Font.BOLD, 20);
    public final Font SETTINGS_LABEL_FONT = new Font("SansSerif", Font.BOLD, 16);

    /**
     * @param resourceFileName relative path of the resource image
     * @return an optional image
     */
    public Optional<BufferedImage> getImage(String resourceFileName) {
        return ResourceFile.getResource(resourceFileName, ImageIO::read);
    }

    /**
     * @param resourceFileName relative path of the resource icon
     * @return an optional icon
     */
    public Optional<ImageIcon> getIcon(String resourceFileName) {
        return ResourceFile.getResource(resourceFileName, ImageIcon::new);
    }

    /**
     * Gets an element of text from the resources according to system langage
     *
     * @param element of text to get in the resources
     */
    public String getTextElement(String element) {
        return textElements.getString(element);
    }

    public String getServerConfig(String element) {
        return serverConfig.getString(element);
    }

    /**
     *  Gets the file of population samples (people ids by regions and zones)
     *
     * @param phase the phase to get samples from
     * @return the file of population samples
     */
    public InputStream getSampleFile(Phases1KG phase) {
        String filename = "samples/" + phase + ".txt";
        return Resource.class.getClassLoader().getResourceAsStream(filename);
    }

    /**
     * Gets the list of people of the selected zones for the given phase
     *
     * @param phase the phase to get the sample from
     * @param selection the zones and region to get the sample from
     * @return the sample (a Set containing people ids)
     * @throws IOException if an error occurred while reading the file
     */
    public Set<String> getSamples(Phases1KG phase, ZoneSelection selection) throws IOException {
        try (var streamReader = new InputStreamReader(getSampleFile(phase));
            var reader = new BufferedReader(streamReader)) {
            return reader.lines().map(line -> line.split("\t"))
                .filter(fields -> selection.isSelected(fields[2], fields[1]))
                .map(fields -> fields[0]).collect(Collectors.toSet());
        }
    }

    /**
     * Gets the VCF URL template for the given phase
     *
     * @param phase1KG the phase to use for getting VCF files
     * @return the URL template
     */
    public String getVcfUrlTemplate(Phases1KG phase1KG) {
        String path = getServerConfig("1kg." + phase1KG + ".path");
        String filenameTemplate = getServerConfig("1kg." + phase1KG + ".filename");
        String host = getServerConfig("1kg.host");
        return host + "/" + path + "/" + filenameTemplate;
    }

    /**
     * Gets the file of chromosome ending positions for the selected hgVersion
     *
     * @param hgVersion the human genome version
     * @return the file of ending positions
     */
    public InputStream getChrEndPositions(HumanGenomeVersions hgVersion) {
        String filename = "chrEndPositions/" + hgVersion + ".txt";
        return Resource.class.getClassLoader().getResourceAsStream(filename);
    }

    /**
     * Gets the end position for the given chromosome
     *
     * @param hgVersion the human genome version
     * @param chrName the name of the chromosome. `1` for example.
     * @return the end position (empty if chromosome not found in the file,
     * or if an error occurred while reading the file)
     */
    public Optional<Integer> getChrEndPosition(HumanGenomeVersions hgVersion, String chrName) {
        try (var streamReader = new InputStreamReader(getChrEndPositions(hgVersion));
            var reader = new BufferedReader(streamReader)) {
            return reader.lines().map(line -> line.split("\t"))
                .filter(fields -> fields[0].equals(chrName))
                .map(fields -> fields[1]).findFirst().map(Integer::parseInt);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Impossible to open file", e);
            return  Optional.empty();
        }
    }
}
