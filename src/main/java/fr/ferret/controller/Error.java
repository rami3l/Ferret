package fr.ferret.controller;

import fr.ferret.utils.Resource;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Use it to create an error message and highlight components
 */
public class Error {
    private final JFrame frame;

    private final StringBuilder errorMessage = new StringBuilder();

    public Error(JFrame frame) {
        this.frame = frame;
    }

    public Error append(String element, Object... args) {
        errorMessage.append("\n ").append(String.format(Resource.getTextElement(element), args));
        return this;
    }

    public Error highlight(JComponent... components) {
        List.of(components).forEach(component -> component
            .setBorder(BorderFactory.createLineBorder(Color.RED, 1)));
        return this;
    }

    public void show() {
        JOptionPane.showMessageDialog(frame, errorMessage,
            Resource.getTextElement("run.error"), JOptionPane.ERROR_MESSAGE);
    }

    // TODO: add stacktrace in the popup
}
