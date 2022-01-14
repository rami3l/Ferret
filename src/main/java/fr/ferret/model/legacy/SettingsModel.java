/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package fr.ferret.model.legacy;

/**
 *
 * @author youne
 */
public class SettingsModel {
    private int version1KG;
    private String output;
    private boolean versionHG;
    private double maf;

    public SettingsModel(int version1KG, String output, boolean versionHG) {
        this.version1KG = version1KG;
        this.output = output;
        this.versionHG = versionHG;
    }

    public int getVersion1KG() {
        return version1KG;
    }

    public void setVersion1KG(int version1KG) {
        this.version1KG = version1KG;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isVersionHG() {
        return versionHG;
    }

    public void setVersionHG(boolean versionHG) {
        this.versionHG = versionHG;
    }

    public double getMaf() {
        return maf;
    }

    public void setMaf(double maf) {
        this.maf = maf;
    }



}
