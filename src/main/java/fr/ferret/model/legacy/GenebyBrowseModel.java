/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package fr.ferret.model.legacy;

/**
 *
 * @author youne
 */
public class GenebyBrowseModel extends GeneModel {
    private String fileLocation;

    public GenebyBrowseModel(String fileAdresse, String[] population) {
        super(fileAdresse, population);
    }



    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

}
