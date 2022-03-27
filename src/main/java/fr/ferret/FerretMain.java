package fr.ferret;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;

/**
 * Main Ferret class displaying the FerretFrame
 */
public class FerretMain {

    public static void main(String[] args) {
        Resource.loadConfig();
        FerretFrame frame = new FerretFrame(); // Show ferret frame
        ExceptionHandler.setFrame(frame);
        // TODO: we need to make the interface responsive before making it resizable
        frame.setResizable(false);
        frame.setVisible(true);
        Resource.updateAssemblyAccessVersions();
    }

}
