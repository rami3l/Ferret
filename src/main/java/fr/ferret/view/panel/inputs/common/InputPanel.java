package fr.ferret.view.panel.inputs.common;

import fr.ferret.utils.Resource;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public abstract class InputPanel extends JPanel {

    protected JTextPane generateHelpSection(String textElement) {
        JTextPane help = new JTextPane();

        help.setText(Resource.getTextElement(textElement));
        help.setEditable(false);
        help.setBorder(BorderFactory.createLineBorder(Resource.HELP_BORDER_COLOR, 2));
        help.setFont(Resource.HELP_LABEL_FONT);

        var doc = help.getStyledDocument();
        var center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        return help;
    }

}
