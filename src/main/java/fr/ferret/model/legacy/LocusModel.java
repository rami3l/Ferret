/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package fr.ferret.model.legacy;

/**
 *
 * @author youne
 */
public class LocusModel extends ElementSaisiModel implements Comparable<LocusModel> {
    private String chromosome;
    private int start;
    private int end;

    public LocusModel(String chromosome, int start, int end) {
        super();
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int chrToInt() {
        if ("X".equals(this.chromosome) || "Y".equals(this.chromosome)) {
            return 23;
        } else {
            return Integer.parseInt(this.chromosome);
        }
    }

    @Override
    public int compareTo(LocusModel o) {
        if (!(this.chromosome.equals(o.chromosome))) {
            return this.chrToInt() - o.chrToInt();
        } else {
            return this.start - o.start;
        }
    }

}
