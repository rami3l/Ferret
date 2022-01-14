/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package fr.ferret.model.legacy;

/**
 *
 * @author youne
 */
public class GenebyNameModel extends GeneModel {
    private String name;

    public GenebyNameModel(String name, String fileAdresse, String[] population) {
        super(fileAdresse, population);
        this.name = name;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
