package fr.ferret.controller.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        // TODO: Open popup with information on error (use Error class defined in InputPanelController)
        logger.log(Level.WARNING, "Error handler: ", throwable);
    }
}
