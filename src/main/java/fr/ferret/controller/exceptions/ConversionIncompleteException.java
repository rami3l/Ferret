package fr.ferret.controller.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class ConversionIncompleteException extends RuntimeException {
    @Getter
    private final List<String> notFound;
}
