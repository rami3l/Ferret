package fr.ferret.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.SwingWorker;

import fr.ferret.utils.Resource;
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

        // TODO Update links and tests
        URL urlLocation = new URL(Resource.getServerConfig("update.url"));

        try(var br = new BufferedReader(new InputStreamReader(urlLocation.openStream()))) {
            String currentString;
            while ((currentString = br.readLine()) != null) {
                String[] updateInformation = currentString.split(":");
                // 2 is the internal version number of this version of Ferret
                if (Integer.parseInt(updateInformation[0]) <= 3) {
                    if ("urgentUpdate".equals(updateInformation[1])) {
                        updateMessage = Resource.getTextElement("update.urgent") + " "
                            + updateInformation[2];
                        needUpdate = true;
                        urgentUpdate = true;
                    } else if ("recommendedUpdate".equals(updateInformation[1])) {
                        updateMessage = Resource.getTextElement("update.recommended") + " "
                            + updateInformation[2];
                        needUpdate = true;
                        urgentUpdate = false;
                    } else if ("noUpdate".equals(updateInformation[1])) {
                        updateMessage = Resource.getTextElement("update.uptodate");
                        needUpdate = false;
                        urgentUpdate = false;
                    }
                }
            }
            if (needUpdate == null || urgentUpdate == null) {
                needUpdate = true;
                urgentUpdate = true;
                updateMessage = Resource.getTextElement("update.error");
            }
        } catch (IOException e) {
            needUpdate = true;
            urgentUpdate = true;
            updateMessage = Resource.getTextElement("update.error");
        }
        return needUpdate;
    }

}
