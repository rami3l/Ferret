package fr.ferret.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ZoneSelection {

    /**
     * The keys represent the selected regions and the values represent the zones selected in each
     * region. A region of which all zones are selected can be represented by an empty list for its
     * value.
     */
    private final List<Zone> selectedZones = new ArrayList<>();
    @Getter
    private boolean allSelected = false;

    public boolean isEmpty() {
        return selectedZones.isEmpty() && !allSelected;
    }

    public boolean isSelected(Zone zone) {
        return allSelected || selectedZones.contains(zone);
    }

    public ZoneSelection add(Zone zone) {
        if (zone instanceof Region region) {
            selectedZones.addAll(region.getZones());
        } else {
            selectedZones.add(zone);
        }
        return this;
    }

    public ZoneSelection selectAll() {
        allSelected = true;
        return this;
    }

    // TODO: ne fonctionne pas quand allSelected (car on a juste changé la valeur du booléen sans ajouter l'ensemble des zones à la sélection)
    public Set<String> getSample() {
        Set<String> people = new HashSet<>();
        selectedZones.forEach(zone -> people.addAll(zone.getPeople()));
        return people;
    }
}
