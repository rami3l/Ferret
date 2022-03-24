package fr.ferret.controller.exceptions;

import java.io.IOException;

public class FileWritingException extends IOException {
    public FileWritingException(Throwable e) {
        super(e);
    }
}
