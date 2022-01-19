package fr.ferret.view.utils;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Util class to deal with resouce files
 */
public class Resource {

    private static final Logger LOG = Logger.getLogger(Resource.class.getName());

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
            LOG.log(Level.WARNING,
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
            LOG.log(Level.WARNING,
                    String.format("Failed to get resource image %s", resourceFileName), e);
        }
        // we return an optional icon (with a null value if impossible to get the icon)
        return Optional.ofNullable(icon);
    }

}
