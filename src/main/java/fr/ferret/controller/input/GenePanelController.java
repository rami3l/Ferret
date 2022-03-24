package fr.ferret.controller.input;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.input.common.FieldOrIdPanelController;
import fr.ferret.controller.state.Error;
import fr.ferret.model.locus.GeneConversion;
import fr.ferret.model.locus.Locus;
import fr.ferret.model.state.PublishingStateProcessus;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.GenePanel;

import javax.swing.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link GenePanel} controller
 */
public class GenePanelController extends FieldOrIdPanelController {

    private static final Logger logger = Logger.getLogger(GenePanelController.class.getName());

    public GenePanelController(FerretFrame frame) {
        // invalidRegex: this is everything except letters and numbers, including underscore
        super(frame, frame.getGenePanel(), ".*[^a-zA-Z0-9\\-].*");
    }

    @Override
    protected boolean confirmContinue(String notFound) {
        return ExceptionHandler.genesNotFoundMessage(notFound);
    }

    @Override
    protected PublishingStateProcessus<List<Locus>> getConversionProcessus(List<String> geneList) {
        var assemblyAccVer = Resource.getAssemblyAccessVersion();
        logger.log(Level.INFO, "Starting gene research using {0} assembly accession version...", assemblyAccVer);
        return new GeneConversion(geneList, assemblyAccVer);
    }


    protected void displayError(boolean geneListInputted, boolean geneFileImported,
            boolean geneFileError, boolean geneFileExtensionError, boolean invalidCharacter,
            boolean popSelected, boolean validWindowSizeEntered) {

        JComponent inputField = panel.getInputField();
        JComponent runButton = panel.getFileSelector().getRunButton();

        var error = new Error(frame).append("run.fixerrors");

        if (!geneListInputted && !geneFileImported) {
            error.append("run.selectgene").highlight(inputField, runButton);
        }
        if (geneFileImported && geneFileError) {
            error.append("run.selectgene.ferr").highlight(runButton);
        }
        if (geneFileImported && geneFileExtensionError) {
            error.append("run.selectgene.fext").highlight(runButton);
        }
        if (geneListInputted && invalidCharacter) {
            error.append("run.selectgene.cerr").highlight(inputField);
        }
        if (geneFileImported && invalidCharacter) {
            error.append("run.selectgene.cerr").highlight(runButton);
        }
        if (!popSelected) {
            error.append("run.selectpop").highlight(frame.getRegionPanel());
        }
        if (!validWindowSizeEntered) {
            error.append("run.selectvari.wsize").highlight(panel.getBpField());
        }
        error.show();
    }
}
