package fr.ferret.model;

import fr.ferret.utils.Resource;
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
     * The name of the zone
     */
    protected final String name;

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
        this.name = Resource.getTextElement("region." + abbrev);
    }

    protected Zone(String abbrev, int nbPeople) {
        this.abbrev = abbrev;
        this.people = new HashSet<>();
        this.nbPeople = nbPeople;
        this.name = Resource.getTextElement("region." + abbrev);
    }

    public static Set<Zone> of(Map<String, Set<String>> zones) {
        return zones.entrySet().stream().map(entry -> new Zone(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet());
    }
}
