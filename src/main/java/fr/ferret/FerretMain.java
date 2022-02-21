package fr.ferret;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.view.FerretFrame;

/**
 * Main Ferret class displaying the FerretFrame
 */
public class FerretMain {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        FerretFrame frame = new FerretFrame(); // Show ferret frame
        frame.setVisible(true);
    }

}
