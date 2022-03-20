package fr.ferret.model;

import fr.ferret.utils.Resource;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SampleSelection {

    /**
     * The keys represent the selected regions and the values represent the zones selected in each
     * region. A region of which all zones are selected can be represented by an empty list for its
     * value.
     */
    private final List<Zone> selectedZones = new ArrayList<>();
    @Getter
    private boolean allSelected = false;

    /**
     * Checks if this SampleSelection is empty
     */
    public boolean isEmpty() {
        return selectedZones.isEmpty() && !allSelected;
    }

    /**
     * Adds recursively all the people of the given {@link Zone} or its child zones (if it is a
     * {@link Region}) to this {@link SampleSelection}
     */
    public SampleSelection add(Zone zone) {
        if (zone instanceof Region region) {
            region.getZones().forEach(this::add);
        } else {
            selectedZones.add(zone);
        }
        return this;
    }

    /**
     * Adds all the people of the given {@link Phase1KG} to this {@link SampleSelection}
     */
    public SampleSelection selectAllFor(Phase1KG phase) {
        allSelected = true;
        Resource.getSample(phase).forEach(this::add);
        return this;
    }

    /**
     * Gets all the people contained in this {@link SampleSelection}
     */
    public Set<String> getSample() {
        Set<String> people = new HashSet<>();
        selectedZones.forEach(zone -> people.addAll(zone.getPeople()));
        return people;
    }
}
