package fr.ferret;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;

/**
 * Main Ferret class displaying the FerretFrame
 */
public class FerretMain {

    public static void main(String[] args) {
        FerretFrame frame = new FerretFrame(); // Show ferret frame
        ExceptionHandler.setFrame(frame);
        frame.setVisible(true);
        Resource.loadConfig();
    }

}
