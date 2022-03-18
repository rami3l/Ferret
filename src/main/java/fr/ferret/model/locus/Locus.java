package fr.ferret.model.locus;

import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.ToString;

/**
 * A locus is a zone of a chromosome (from start to end).<br>
 * We can get the locus of a gene using a {@link GeneConversion}.<br>
 * We can get the locus of a variant using a {@link VariantConversion}.
 */
@Getter
@ToString
public final class Locus {
    private static final Logger logger = Logger.getLogger(Locus.class.getName());
    private final String chromosome;
    private final int start;
    private final int end;

    public Locus(String chromosome, int start, int end) {
        this.chromosome = chromosome;
        if (start > end) {
            this.start = end;
            this.end = start;
            logger.log(Level.INFO, "start > end. Inverting : {0}", this);
        } else {
            this.start = start;
            this.end = end;
        }
    }

    public Locus withWindow(int windowSize) {
        return new Locus(chromosome, start - windowSize, end + windowSize);
    }

}
