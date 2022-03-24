package fr.ferret.controller.input.common;

import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import fr.ferret.model.utils.FileReader;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.inputs.FieldOrFilePanel;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class FieldOrIdPanelController extends NeedingConversionPanelController {

    /** The panel which is controlled by this controller */
    protected final FieldOrFilePanel panel;
    /** Regex to validate the inputted elements (from field or file) */
    private final String invalidRegex;

    protected FieldOrIdPanelController(FerretFrame frame, FieldOrFilePanel panel,
        String invalidRegex) {
        super(frame);
        this.panel = panel;
        this.invalidRegex = invalidRegex;
    }

    protected abstract void displayError(boolean snpListInputted, boolean snpFileImported,
        boolean snpFileError, boolean snpFileExtensionError, boolean invalidCharacter,
        boolean popSelected, boolean validWindowSizeEntered);

    public void validateInfoAndRun() {
        // Reset the borders
        panel.getInputField().setBorder(null);
        panel.getFileSelector().getRunButton().setBorder(null);
        panel.getBpField().setBorder(null);

        JTextField inputField = panel.getInputField();

        // Selected populations for the model
        var populations = getSelectedPopulations();
        boolean popSelected = !populations.isEmpty();

        // List which will contain the genes (from field or file)
        List<String> enteredElements = new ArrayList<>();

        String elements = inputField.getText();

        // Did the user input a list of gene
        boolean areElementsEntered = elements.length() > 0;

        // Did the user import a csv file
        String selectedFile = panel.getFileSelector().getSelectedFile() == null ?
            null :
            panel.getFileSelector().getSelectedFile().getAbsolutePath();
        boolean isFileSelected = selectedFile != null;

        // Are they errors in imported file (impossible to read, invalid extension or invalid
        // content)
        boolean fileError = false;
        boolean fileExtensionError = false;
        boolean invalidCharacter = false;

        // bpWindow field must be an integer or be empty (0 as default value)
        String bpWindowText = panel.getBpField().getText();
        boolean validWindowSizeEntered = true;
        int bpWindow = 0;

        if(!bpWindowText.isBlank()) {
            try {
                bpWindow = Integer.parseInt(bpWindowText);
            } catch (Exception e) {
                validWindowSizeEntered = false;
            }
        }

        // Loads the elements from the file if applicable
        if (isFileSelected) {
            try {
                enteredElements.addAll(FileReader.readCsvLike(selectedFile, invalidRegex));
            } catch (FileFormatException e) {
                fileExtensionError = true;
            } catch (FileContentException e) {
                invalidCharacter = true;
            } catch (IOException e) {
                fileError = true;
            }
        }

        // Loads the elements from the input field if applicable
        if (areElementsEntered) {
            elements = elements.replace(" ", "");
            invalidCharacter = elements.replace(",", "").matches(invalidRegex);
            if (elements.endsWith(",")) {
                elements = elements.substring(0, elements.length() - 1);
            }
            enteredElements.addAll(Arrays.asList(elements.split(",")));
        }

        if ((areElementsEntered || (isFileSelected && !fileError && !fileExtensionError)) && !invalidCharacter && validWindowSizeEntered && popSelected) {

            convertAndDownloadVcf(populations, enteredElements, bpWindow);

        } else {
            displayError(areElementsEntered, isFileSelected, fileError, fileExtensionError,
                invalidCharacter, popSelected, validWindowSizeEntered);
        }
    }

}
