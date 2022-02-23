package fr.ferret.controller.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;
import fr.ferret.controller.Error;
import lombok.experimental.UtilityClass;

import javax.swing.*;

@UtilityClass
public class ExceptionHandler {

    private final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    private JFrame f;

    public void setFrame(JFrame frame) {
        f = frame;
    }

    public void connectionError(Throwable throwable) {
        logger.log(Level.WARNING, "No internet connection", throwable);
        new Error(f).append("error.connection").show();
    }

    public void vcfStreamingError(Throwable throwable) {
        logger.log(Level.WARNING, "VCF streaming error", throwable);
        new Error(f).append("error.vcfstreaming").show();
    }

    public void ressourceAccessError(Throwable throwable) {
        logger.log(Level.WARNING, "Resource access error", throwable);
        new Error(f).append("error.resource").show();
    }

}
