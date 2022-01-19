package fr.ferret;

import java.util.Locale;
import java.util.ResourceBundle;
import fr.ferret.controller.settings.FerretConfig;
import fr.ferret.view.FerretFrame;
import lombok.Getter;

/**
 * Main Ferret class displaying the FerretFrame
 */
public class FerretMain {

    /**
     * Program resources (languages and properties)
     */
    @Getter
    private static ResourceBundle locale;

    /**
     * Program settings
     */
    @Getter
    private static final FerretConfig config = new FerretConfig();

    public static void main(String[] args) {
        locale = ResourceBundle.getBundle("ferret", Locale.getDefault());
        FerretFrame frame = new FerretFrame(); // Show ferret frame
        frame.setVisible(true);
        getLocale();
    }

}
