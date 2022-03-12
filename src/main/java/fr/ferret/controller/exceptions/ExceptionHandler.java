package fr.ferret.controller.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import fr.ferret.controller.Error;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionHandler {

    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    private static JFrame f;

    public static void setFrame(JFrame frame) {
        f = frame;
    }

    public static void connectionError(Throwable throwable) {
        logger.log(Level.WARNING, "No internet connection", throwable);
        new Error(f).append("error.connection").show();
    }

    public static void noIdFoundError(Throwable throwable) {
        logger.log(Level.WARNING, "No valid id for this request to ncbi server", throwable);
        new Error(f).append("error.noIdFound").show();
    }

    public static void vcfStreamingError(Throwable throwable) {
        logger.log(Level.WARNING, "VCF streaming error", throwable);
        new Error(f).append("error.vcfStreaming").show();
    }

    public static void fileWritingError(Throwable throwable) {
        logger.log(Level.WARNING, "File writing error", throwable);
        new Error(f).append("error.fileWriting").show();
    }

    public static void ressourceAccessError(Throwable throwable) {
        logger.log(Level.WARNING, "Resource access error", throwable);
        new Error(f).append("error.resource").show();
    }

    public void unknownError(Throwable throwable) {
        logger.log(Level.WARNING, "Unknown error", throwable);
        new Error(f).append("error.unknown", throwable).show();
    }

}
