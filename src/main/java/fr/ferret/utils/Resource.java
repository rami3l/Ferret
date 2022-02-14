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
import fr.ferret.controller.settings.Phases1KG;
import fr.ferret.model.ZoneSelection;
import jdk.jshell.spi.ExecutionControl;
import lombok.experimental.UtilityClass;

/**
 * Util class to deal with resouce files
 */
@UtilityClass
public class Resource {
    /**
     * program settings
     */
    public  final FerretConfig CONFIG = new FerretConfig();

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
        BufferedImage img = null;
        try {
            // we try to read the image from the resource file
            img = ImageIO.read(Resource.class.getResource(resourceFileName));
        } catch (Exception e) {
            logger.log(Level.WARNING,
                    String.format("Failed to get resource image %s", resourceFileName), e);
        }
        // we return an optional image (with a null value if impossible to get the image)
        return Optional.ofNullable(img);
    }

    /**
     * @param resourceFileName relative path of the resource icon
     * @return an optional icon
     */
    public Optional<ImageIcon> getIcon(String resourceFileName) {
        ImageIcon icon = null;
        try {
            // we try to read the icon from the resource file
            icon = new ImageIcon(Resource.class.getResource(resourceFileName));
        } catch (Exception e) {
            logger.log(Level.WARNING,
                    String.format("Failed to get resource image %s", resourceFileName), e);
        }
        // we return an optional icon (with a null value if impossible to get the icon)
        return Optional.ofNullable(icon);
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

    public String getPhase(Phases1KG phase1KG) {
        return switch (phase1KG) {
            case V1 -> "phase1";
            case V3 -> "phase3";
            default -> ""; // TODO: throw not implemented exception (phase NYGC_30X not implemented)
                           // ?
        };
    }

    public InputStream getSampleFile(Phases1KG phase) {
        String filename = "samples/" + getPhase(CONFIG.getSelectedVersion()) + ".txt";
        return Resource.class.getClassLoader().getResourceAsStream(filename);
    }

    public Set<String> getSamples(Phases1KG phase, ZoneSelection selection)
            throws IOException {
        try (var streamReader = new InputStreamReader(getSampleFile(phase));
                var reader = new BufferedReader(streamReader)) {
            return reader.lines().map(line -> line.split("\t"))
                    .filter(fields -> selection.isSelected(fields[2], fields[1]))
                    .map(fields -> fields[0]).collect(Collectors.toSet());
        }
    }
}
