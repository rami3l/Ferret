package fr.ferret.controller.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class VariantsNotFoundException extends RuntimeException {
    @Getter
    private final List<String> notFound;
}
