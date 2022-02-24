package fr.ferret.model;

public record Locus(String chromosome, int start, int end) {
    @Override public String toString() {
        return "Locus{" + "chromosome='" + chromosome + '\'' + ", start=" + start + ", end=" + end
            + '}';
    }
}
