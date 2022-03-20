package fr.ferret.model;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

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
public final class Region extends Zone {

    /**
     * The zones (populations) of the region
     */
    private final Set<Zone> zones;

    /**
     * Creates a new Region
     * @param abbrev The abbreviation of the region
     * @param zones The zones (populations) of the region
     */
    public Region(String abbrev, Set<Zone> zones) {
        super(abbrev, zones.stream().mapToInt(Zone::getNbPeople).sum());
        this.zones = zones;
        zones.forEach(zone -> zone.setRegion(this));
    }

    public Region(String abbrev, int nbPeople) {
        super(abbrev, nbPeople);
        this.zones = new HashSet<>();
    }

    public static Set<Region> fromSample(Map<String, Map<String, Set<String>>> regions) {
        return regions.entrySet().stream()
            .map(entry -> new Region(entry.getKey(), Zone.of(entry.getValue())))
            .collect(Collectors.toSet());
    }

}
