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

    // TODO: move to a resource file
    private static final Map<String, String> names = Map.of(
        "AFR", "Afrique",
        "EUR", "Europe",
        "EAS", "East Asia",
        "AMR", "America",
        "SAS", "South Asia"
    );

    /**
     * The region name
     */
    private final String name;


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
        this.name = names.get(abbrev);
        this.zones = zones;
        zones.forEach(zone -> zone.setRegion(this));
    }

    public Region(String abbrev, int nbPeople) {
        // TODO: get name from a resource file
        super(abbrev, nbPeople);
        this.name = "";
        this.zones = new HashSet<>();
    }

    public static Set<Region> fromSample(Map<String, Map<String, Set<String>>> regions) {
        return regions.entrySet().stream()
            .map(entry -> new Region(entry.getKey(), Zone.of(entry.getValue())))
            .collect(Collectors.toSet());
    }

}
