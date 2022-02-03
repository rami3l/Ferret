package fr.ferret.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.SwingWorker;
import lombok.Getter;

/**
 * An update checker for Ferret <br>
 * Not tested
 */
@Getter
public class UpdateChecker extends SwingWorker<Boolean, Object> {
    private Boolean needUpdate = null;
    private Boolean urgentUpdate = null;
    private String updateMessage = null;

    @Override
    protected Boolean doInBackground() throws Exception {
        try {
            // TODO Update links and tests
            URL urlLocation =
                    new URL("https://webspace.princeton.edu/users/taverner/updateFerret.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(urlLocation.openStream()));
            String currentString;
            while ((currentString = br.readLine()) != null) {
                String[] updateInformation = currentString.split(":");
                if (Integer.parseInt(updateInformation[0]) <= 3) { // 2 is the internal version
                                                                   // number of this version of
                                                                   // Ferret
                    if (updateInformation[1].equals("urgentUpdate")) {
                        updateMessage =
                                "Urgent update. Ferret may not be functional until you update: "
                                        + updateInformation[2];
                        needUpdate = true;
                        urgentUpdate = true;
                    } else if (updateInformation[1].equals("recommendedUpdate")) {
                        updateMessage = "Recommended update: " + updateInformation[2];
                        needUpdate = true;
                        urgentUpdate = false;
                    } else if (updateInformation[1].equals("noUpdate")) {
                        updateMessage = "Ferret is up to date";
                        needUpdate = false;
                        urgentUpdate = false;
                    }
                }
            }
            if (needUpdate == null || urgentUpdate == null) {
                needUpdate = true;
                urgentUpdate = true;
                updateMessage =
                        "Unable to contact update server. Try again later or update Ferret.";
            }
        } catch (IOException e) {
            needUpdate = true;
            urgentUpdate = true;
            updateMessage = "Unable to contact update server. Try again later or update Ferret.";
        }
        return needUpdate;
    }

}
