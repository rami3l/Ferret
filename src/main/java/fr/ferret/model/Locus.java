package fr.ferret.model;

/**
 * A locus is a zone of a given chromosome (from start to end).
 * We can get the locus of a gene with a {@link LocusBuilder}.
 *
 * @param chromosome The chromosome
 * @param start      The start position of the locus in the chromosome
 * @param end        The end position of the locus in the chromosome
 */
public record Locus(String chromosome, int start, int end) {

    @Override
    public String toString() {
        return "Locus{" + "chromosome='" + chromosome + '\'' + ", start=" + start + ", end=" + end
            + '}';
    }
}
