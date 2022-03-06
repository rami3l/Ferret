package fr.ferret.model.conversions;

import htsjdk.variant.variantcontext.VariantContext;

public record RefFrequencyPair(
        /**
         * The relative frequency of the {@code REF} allele of this variant among all its samples'
         * genotypes.
         */
        double frequency,
        /**
         * The number of all completely called (without {@code '.'}) number of chromosomes
         * ({@code alleles * 2}) of this variant among among all its samples' genotypes.
         */
        int observations) {
    /**
     * Generates a {@code RefFrequencyPair} from a {@code VariantContext} by iterating through all
     * its samples.
     */
    public static RefFrequencyPair of(VariantContext variant) {
        var samples = variant.getSampleNames();
        // The 0|0 instances.
        var homRef = 0;
        // The 0|1, 0/2, ... instances.
        var hetRef = 0;
        // All instances without '.'.
        var calledNonMixed = 0;
        for (var sample : samples) {
            var genotype = variant.getGenotype(sample);
            if (genotype.isHomRef()) {
                homRef++;
            }
            if (genotype.isHet() && !genotype.isHetNonRef()) {
                hetRef++;
            }
            if (genotype.isCalled() && !genotype.isMixed()) {
                calledNonMixed++;
            }
        }
        var freq = calledNonMixed == 0
                // Handle potential division by zero.
                ? 0
                // The real formula is here...
                : (2 * homRef + hetRef) / (2 * (double) calledNonMixed);
        return new RefFrequencyPair(freq, calledNonMixed);
    }

    public String toString() {
        return String.format("%.4f\t%d", frequency, observations);
    }
}
