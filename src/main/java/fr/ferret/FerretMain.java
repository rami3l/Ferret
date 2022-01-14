package fr.ferret;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import fr.ferret.controller.settings.FerretConfig;
import fr.ferret.view.FerretFrame;

/**
 * Main Ferret class displaying the FerretFrame
 */
public class FerretMain {
    /**
     * The logger
     */
    private static final Logger log = Logger.getLogger("Ferret");

    /**
     * Program resources (languages and properties)
     */
    private static ResourceBundle locale;

    /**
     * Program settings
     */
    private static final FerretConfig config = new FerretConfig();

    public static void main(String[] args) {
        locale = ResourceBundle.getBundle("ferret", Locale.getDefault());
        FerretFrame frame = new FerretFrame(); // Show ferret frame
        frame.setVisible(true);
    }

    /**
     * @return The logger
     */
    public static Logger getLog() {
        return log;
    }

    /**
     * @return Program resources (languages and properties)
     */
    public static ResourceBundle getLocale() {
        return locale;
    }

    /**
     * @return Program settings
     */
    public static FerretConfig getConfig() {
        return config;
    }
}
