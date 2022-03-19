package fr.ferret.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class Zone {

    /**
     * The abbreviation of the zone name
     */
    protected final String abbrev;

    /**
     * The size of the population from this zone
     */
    protected final int nbPeople;

    private final Set<String> people;

    /**
     * The region containing this zone if applicable
     */
    @Setter
    private Region region;

    public Zone(String abbrev, Set<String> people) {
        this.abbrev = abbrev;
        this.people = people;
        this.nbPeople = people.size();
    }

    protected Zone(String abbrev, int nbPeople) {
        this.abbrev = abbrev;
        this.people = new HashSet<>();
        this.nbPeople = nbPeople;
    }

    public static Set<Zone> of(Map<String, Set<String>> zones) {
        return zones.entrySet().stream().map(entry -> new Zone(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet());
    }
}
