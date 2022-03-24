package fr.ferret.model;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A <strong>world region</strong> of the 1KG project. <br>
 * It is a {@link Zone} containing other zones.<br>
 * Currently, the world regions are "All populations", "Africa", "Europe", "East asia", "America"
 * and "South asia".
 */
@Getter
public final class Region extends Zone {

    /**
     * The zones (populations) of the region
     */
    private final Set<Zone> zones;

    /**
     * Creates a new Region containing {@link Zone zones}
     * @param abbrev The abbreviation of the region
     * @param zones The zones (populations) of the region
     */
    public Region(String abbrev, Set<Zone> zones) {
        super(abbrev, zones.stream().mapToInt(Zone::getNbPeople).sum());
        this.zones = zones;
        zones.forEach(zone -> zone.setRegion(this));
    }

    /**
     * Creates a Region with a given number of people, but without containing zones. It is used to
     * represent the "All populations" option in the interface
     *
     * @param abbrev The abbreviation of the region
     * @param nbPeople The population size
     */
    public Region(String abbrev, int nbPeople) {
        super(abbrev, nbPeople);
        this.zones = new HashSet<>();
    }

    /**
     * Creates a {@link Set} of {@link Region regions} from a sample map. The keys of this map are
     * the abbreviations of the regions, and the values are maps corresponding to the zones
     * contained in the regions. See {@link Zone#of} for more information about these maps
     *
     * @param regions The sample {@link Map} to create the regions from
     * @return The {@link Set} of regions.
     */
    public static Set<Region> fromSample(Map<String, Map<String, Set<String>>> regions) {
        return regions.entrySet().stream()
            .map(entry -> new Region(entry.getKey(), Zone.of(entry.getValue())))
            .collect(Collectors.toSet());
    }

}
