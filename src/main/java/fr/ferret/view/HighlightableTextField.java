package fr.ferret.view;

import lombok.Getter;

import javax.swing.*;

/**
 * This class encapsulate a {@link JTextField} so that you can change highlight its border without
 * destroying the border of the JTextField <br>
 * TODO: we should use it for others text field than bpField, but it needs to fix the auto size issue
 */
public class HighlightableTextField extends JPanel {
    @Getter private final JTextField field;

    public HighlightableTextField() {
        field = new JTextField();
        add(field);
    }

    public HighlightableTextField(int columns) {
        field = new JTextField(columns);
        add(field);
    }

    public HighlightableTextField(String text) {
        field = new JTextField(text);
        add(field);
    }

    public String getText() {
        return field.getText();
    }
}
