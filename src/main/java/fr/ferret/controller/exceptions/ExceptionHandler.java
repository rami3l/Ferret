package fr.ferret.controller.exceptions;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.FileSystemException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.*;

import fr.ferret.controller.state.Error;
import fr.ferret.model.Phase1KG;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionHandler {

    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    private static JFrame f;

    public static void setFrame(JFrame frame) {
        f = frame;
    }

    public static void show(Throwable error) {
        try {
            throw error;
        } catch (UnknownHostException e) {
            ExceptionHandler.connectionError(e);
        } catch (NoIdFoundException e) {
            ExceptionHandler.noIdFoundError(e);
        } catch (VcfStreamingException e) {
            ExceptionHandler.vcfStreamingError(e);
        } catch (FileWritingException e) {
            ExceptionHandler.fileWritingError(e);
        } catch (Throwable e) {
            ExceptionHandler.unknownError(e);
        }
    }

    public static void connectionError(Throwable throwable) {
        logger.log(Level.WARNING, "No internet connection", throwable);
        new Error(f).append("error.connection").show();
    }

    public boolean genesNotFoundMessage(String genesNotFound) {
        logger.log(Level.WARNING, "Failed to convert those genes (ignored from vcf): {0}", genesNotFound);
        return new Error(f).append("error.genesNotFound", genesNotFound).confirm();
    }

    public boolean variantsNotFoundMessage(String variantsNotFound) {
        logger.log(Level.WARNING, "Failed to convert those variants (ignored from vcf): {0}", variantsNotFound);
        return new Error(f).append("error.genesNotFound", variantsNotFound).confirm();
    }

    public static void noIdFoundError(Throwable throwable) {
        logger.log(Level.WARNING, "No valid id for this request to ncbi server", throwable);
        new Error(f).append("error.noIdFound").show();
    }

    public static void noLocusFoundError() {
        logger.log(Level.WARNING, "The conversion failed for all entered elements");
        new Error(f).append("error.noLocusFound").show();
    }

    public static void vcfStreamingError(Throwable throwable) {
        logger.log(Level.WARNING, "VCF streaming error", throwable);
        new Error(f).append("error.vcfStreaming").show();
    }

    public static void fileWritingError(Throwable throwable) {
        logger.log(Level.WARNING, "File writing error", throwable);
        new Error(f).append("error.fileWriting").show();
    }

    public static void ressourceAccessError(@Nullable Throwable throwable, @Nullable String name) {
        var arg = name == null ? "" : "(" + name + ")";
        logger.log(Level.SEVERE, String.format("Resource access error %s", arg), throwable);
        new Error(f).append("error.resource", arg).show();
    }

    public static LinkedHashMap<Phase1KG, String> phaseInitialisationError() {
        new Error(f).append("error.phaseInitialisation").show();
        return new LinkedHashMap<>();
    }

    public static void phaseNotFoundError() {
        new Error(f).append("error.phaseNotFound").show();
    }

    public void unknownError(Throwable throwable) {
        logger.log(Level.WARNING, "Unknown error", throwable);
        new Error(f).append("error.unknown", throwable).show();
    }
}
