package fr.ferret.controller.input;

import fr.ferret.controller.exceptions.ExceptionHandler;
import fr.ferret.controller.input.common.FieldOrIdPanelController;
import fr.ferret.controller.state.Error;
import fr.ferret.model.locus.Locus;
import fr.ferret.model.locus.VariantConversion;
import fr.ferret.model.state.PublishingStateProcessus;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.VariantPanel;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link VariantPanel} controller
 */
public class VariantPanelController extends FieldOrIdPanelController {

    private static final Logger logger = Logger.getLogger(VariantPanelController.class.getName());

    public VariantPanelController(FerretFrame frame) {
        // invalidRegex: this is everything except numbers
        super(frame, frame.getVariantPanel(), ".*\\D.*");
    }

    @Override
    protected boolean confirmContinue(String notFound) {
        return ExceptionHandler.variantsNotFoundMessage(notFound);
    }

    @Override
    protected PublishingStateProcessus<List<Locus>> getConversionProcessus(List<String> variantList) {
        var hgVersion = Resource.config().getSelectedHumanGenome().toGRC();
        logger.log(Level.INFO, "Starting gene research using {0} HG version...", hgVersion);
        return new VariantConversion(variantList, hgVersion);
    }

    // TODO: refacto: create one super method in FieldOrIdPanelController
    protected void displayError(boolean snpListInputted, boolean snpFileImported,
        boolean snpFileError, boolean snpFileExtensionError, boolean invalidCharacter,
        boolean popSelected, boolean validWindowSizeEntered) {

        var error = new Error(frame).append("run.fixerrors");

        var idSelector = panel.getInputField();
        var runButton = panel.getFileSelector().getRunButton();

        if (!snpListInputted && !snpFileImported) {
            error.append("run.selectvari").highlight(idSelector, runButton);
        }
        if (snpFileImported && snpFileError) {
            error.append("run.selectvari.ferr").highlight(runButton);
        }
        if (snpFileImported && snpFileExtensionError) {
            error.append("run.selectvari.fext").highlight(runButton);
        }
        if ((snpListInputted || snpFileImported) && invalidCharacter) {
            error.append("run.selectvari.cerr")
                    .highlight(snpListInputted ? idSelector : runButton);
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
