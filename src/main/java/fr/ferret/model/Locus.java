package fr.ferret.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * A locus is a zone of a given chromosome (from start to end).
 * We can get the locus of a gene with a {@link LocusBuilder}.
 *
 */
@AllArgsConstructor
@Getter
@ToString
public final class Locus {

    private final String chromosome;
    private final int start;
    private final int end;

}
