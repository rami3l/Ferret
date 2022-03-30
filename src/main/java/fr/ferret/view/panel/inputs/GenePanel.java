package fr.ferret.view.panel.inputs;

import fr.ferret.view.panel.inputs.common.FieldOrFilePanel;
import lombok.Getter;

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
