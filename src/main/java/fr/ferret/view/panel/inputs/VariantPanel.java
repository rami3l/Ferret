package fr.ferret.view.panel.inputs;

import lombok.Getter;

/**
 * The variant panel of Ferret <br>
 * Selection of the gene variants parameters
 */
@Getter
public class VariantPanel extends FieldOrFilePanel {

    /**
     * Creates the variant panel
     */
    public VariantPanel() {
        super("variant.input", "variant.help");
    }

}
