package fr.ferret.view.panel.inputs;

import fr.ferret.utils.Resource;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * The gene panel of Ferret <br>
 * Selection of the genes parameters
 */
@Getter
public class GenePanel extends FieldOrFilePanel {

    /**
     * Creates the gene panel
     */
    public GenePanel() {
        super("gene.input", "gene.help");
    }

}
