/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package fr.ferret.model;

/**
 *
 * @author youne
 */
public class VariantbyIDModel extends VariantModel {
    private int id;

    public VariantbyIDModel(int id, String fileAdresse, String[] population) {
        super(fileAdresse, population);
        this.id = id;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
