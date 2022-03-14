package fr.ferret.model.locus;

import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A locus is a zone of a given chromosome (from start to end). We can get the locus of a gene with
 * a {@link LocusBuilding}.
 *
 */
@Getter
@ToString
@EqualsAndHashCode
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

}
