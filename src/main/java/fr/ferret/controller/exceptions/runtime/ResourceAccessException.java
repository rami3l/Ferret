package fr.ferret.controller.exceptions.runtime;

public class ResourceAccessException extends RuntimeException {
    public ResourceAccessException(Throwable cause) {
        super(cause);
    }
}
