package fr.ferret.model.conversions;

public record RefFrequencyPair(double frequency, int observations) {
    public String toString() {
        return String.format("%.4f\t%d", frequency, observations);
    }
}
