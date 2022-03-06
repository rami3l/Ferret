package fr.ferret.model;

public record Locus(String chromosome, int start, int stop) {

    @Override
    public String toString() {
        return "Locus{" + "chromosome='" + chromosome + '\'' + ", start=" + start + ", stop=" + stop
                + '}';
    }
}
