/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package fr.ferret.model;

/**
 *
 * @author youne
 */
public class ElementSaisiModel {
    protected String fileAdresse;
    protected String[] population;

    public ElementSaisiModel(String fileAdresse, String[] population) {
        this.fileAdresse = fileAdresse;
        this.population = population;
    }

    public ElementSaisiModel() {}


    public String getFileAdresse() {
        return fileAdresse;
    }

    public void setFileAdresse(String fileAdresse) {
        this.fileAdresse = fileAdresse;
    }

    public String[] getPopulation() {
        return population;
    }

    public void setPopulation(String[] population) {
        this.population = population;
    }

    public boolean contain(String s) {
        boolean res = false;
        for (String popul : this.population) {
            if (popul.equals(s)) {
                res = true;
            }
        }
        return res;
    }

}
