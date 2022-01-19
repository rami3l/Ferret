package fr.ferret;

import fr.ferret.controller.settings.FerretConfig;
import fr.ferret.view.FerretFrame;
import lombok.Getter;

/**
 * Main Ferret class displaying the FerretFrame
 */
public class FerretMain {


    /**
     * Program settings
     */
    @Getter
    private static final FerretConfig config = new FerretConfig();

    public static void main(String[] args) {
        FerretFrame frame = new FerretFrame(); // Show ferret frame
        frame.setVisible(true);
    }



}
