package fr.ferret.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import fr.ferret.controller.settings.FerretConfig;

/**
 * Util class to deal with resouce files
 */
public class Resource {


    /** program settings */
    public static final FerretConfig CONFIG = new FerretConfig();

    private static final Logger logger = Logger.getLogger(Resource.class.getName());

    /** text elements for the interface */
    private static final ResourceBundle textElements =
            ResourceBundle.getBundle("ferret", Locale.getDefault());

    public static final Color TITLE_COLOR = new Color(18, 0, 150);
    public static final Color ZONE_LABEL_COLOR = new Color(131, 55, 192);
    public static final Color PANEL_BORDER_COLOR = new Color(131, 55, 192, 140);
    public static final Color BUTTON_COLOR = new Color(201, 157, 240);
    public static final Color LINK_STANDARD_COLOR = new Color(40, 100, 255);
    public static final Color LINK_HOVER_COLOR = new Color(255, 0, 0);
    public static final Color LINK_ACTIVE_COLOR = new Color(128, 0, 128);

    // utils should not be instanciated
    private Resource() {}

    /**
     * 
     * @param resourceFileName relative path of the resource image
     * @return an optional image
     */
    public static Optional<BufferedImage> getImage(String resourceFileName) {
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
     * 
     * @param resourceFileName relative path of the resource icon
     * @return an optional icon
     */
    public static Optional<ImageIcon> getIcon(String resourceFileName) {
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
    public static String getTextElement(String element) {
        return textElements.getString(element);
    }

}
