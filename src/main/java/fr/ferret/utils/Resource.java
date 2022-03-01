package fr.ferret.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
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

import static fr.ferret.utils.ResourceFile.*;

/**
 * Util class to deal with resouce files
 */
@UtilityClass
public class Resource {

    private static final Logger logger = Logger.getLogger(Resource.class.getName());

    /** program settings */
    public static final FerretConfig CONFIG = new FerretConfig();

    /** text elements for the interface */
    private static final ResourceBundle textElements =
            ResourceBundle.getBundle("ferret", Locale.getDefault());

    /** application configuration */
    private static final ResourceBundle serverConfig = ResourceBundle.getBundle("server");

    public static final Color TITLE_COLOR = new Color(18, 0, 150);
    public static final Color ZONE_LABEL_COLOR = new Color(131, 55, 192);
    public static final Color PANEL_BORDER_COLOR = new Color(131, 55, 192, 140);
    public static final Color BUTTON_COLOR = new Color(201, 157, 240);
    public static final Color LINK_STANDARD_COLOR = new Color(40, 100, 255);
    public static final Color LINK_HOVER_COLOR = new Color(255, 0, 0);
    public static final Color LINK_ACTIVE_COLOR = new Color(128, 0, 128);

    public static final Font TITLE_FONT = new Font("Calibri", Font.BOLD, 24);
    public static final Font ZONE_LABEL_FONT = new Font("Calibri", Font.BOLD, 20);
    public static final Font SETTINGS_LABEL_FONT = new Font("SansSerif", Font.BOLD, 16);

    /**
     * @param resourceFileName relative path of the resource image
     * @return an optional image
     */
    public static Optional<BufferedImage> getImage(String resourceFileName) {
        return getResource(resourceFileName, ImageIO::read);
    }

    /**
     * @param resourceFileName relative path of the resource icon
     * @return an optional icon
     */
    public static Optional<ImageIcon> getIcon(String resourceFileName) {
        return getResource(resourceFileName, ImageIcon::new);
    }

    /**
     * Gets an element of text from the resources according to system langage
     *
     * @param element of text to get in the resources
     */
    public static String getTextElement(String element) {
        return textElements.getString(element);
    }

    public static String getServerConfig(String element) {
        return serverConfig.getString(element);
    }

    /**
     * Gets the list of people of the selected zones for the given phase
     *
     * @param phase the phase to get the sample from
     * @param selection the zones and region to get the sample from
     * @return the sample (a Set containing people ids)
     * @throws IOException if an error occurred while reading the file
     */
    public static Set<String> getSamples(Phases1KG phase, ZoneSelection selection) throws IOException {
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
    public static String getVcfUrlTemplate(Phases1KG phase1KG) {
        String path = getServerConfig("1kg." + phase1KG + ".path");
        String filenameTemplate = getServerConfig("1kg." + phase1KG + ".filename");
        String host = getServerConfig("1kg.host");
        return host + "/" + path + "/" + filenameTemplate;
    }

    /**
     * Gets the end position for the given chromosome
     *
     * @param hgVersion the human genome version
     * @param chrName the name of the chromosome. `1` for example.
     * @return the end position (empty if chromosome not found in the file,
     * or if an error occurred while reading the file)
     */
    public static Optional<Integer> getChrEndPosition(HumanGenomeVersions hgVersion, String chrName) {
        try (var streamReader = new InputStreamReader(getChrEndPositionsFile(hgVersion));
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
