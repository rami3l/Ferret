package fr.ferret.model;

import java.util.Arrays;
import lombok.Getter;

/**
 * A <strong>world region</strong> of the 1KG project. <br>
 * Currently, the world regions are "All populations", "Africa", "Europe", "East asia", "America"
 * and "South asia". <br>
 * <br>
 *
 * The <strong>zones</strong> are the populations inside these world regions. For Europe it's EUR,
 * CEU, GBR, FIN, IBS and TSI.
 */
@Getter
public class Region {
    /**
     * The region name
     */
    private final String name;
    /**
     * The zones (populations) of the region
     */
    private final String[] zones;
    /**
     * The individuals count for each zone, respectively
     */
    private final int[] individualCount;

    /**
     * Creates a new Region
     *
     * @param name The name of the region (for translation)
     * @param zones The zones (populations) of the region
     * @param individualCount The individuals count for each zone, respectively
     */
    public Region(String name, String[] zones, int[] individualCount) {
        this.name = name;
        this.zones = zones;
        this.individualCount = individualCount;
        assert zones.length == Arrays.stream(individualCount)
                .count() : "Zones length doesn't match to individuals count";
    }
}
