package fr.ferret.model;

import fr.ferret.utils.Resource;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A <strong>world zone</strong> of the 1KG project. <br>
 * It contains a set of people, a name and an abbrev, and may be part of a {@link Region}.<br><br>
 * For example the Europe {@link Region} (EUR) should contain the following The <strong>zones</strong> :
 * CEU, GBR, FIN, IBS and TSI.<br> This can vary according to the selected {@link Phase1KG} phase.
 */
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
     * The size of the population from this zone (each String of the Set is the id of a people,
     * like HG00121)
     */
    protected final int nbPeople;

    /**
     * The people contained in the zone
     */
    private final Set<String> people;

    /**
     * The region containing this zone if applicable
     */
    @Setter
    private Region region;

    /**
     * Creates a Zone containing people
     *
     * @param abbrev The abbreviation of the zone
     * @param people The {@link Set} of people to include in the zone (each String of the Set is the
     *              id of a people, like HG00121)
     */
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

    /**
     * Creates a {@link Set} of {@link Zone zones} from a sample map. The keys of this map are the
     * abbreviations of the zones, and the values are sets of {@link String} corresponding to the
     * people contained in each zone (each String is the id of a people, like HG00121)
     *
     * @param zones The sample {@link Map} to create the zones from
     * @return The {@link Set} of zones.
     */
    public static Set<Zone> of(Map<String, Set<String>> zones) {
        return zones.entrySet().stream().map(entry -> new Zone(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet());
    }
}
