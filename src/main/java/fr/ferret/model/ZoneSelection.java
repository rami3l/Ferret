package fr.ferret.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZoneSelection {

    /**
     * The keys represent the selected regions and the values represent the zones selected in each
     * region. A region of which all zones are selected can be represented by an empty list for its
     * value.
     */
    private final Map<String, List<String>> selectedZones = new HashMap<>();
    private boolean allSelected = false;

    public boolean isEmpty() {
        return selectedZones.isEmpty();
    }

    public boolean isSelected(String region, String zone) {
        return allSelected
                || (selectedZones.containsKey(region) && (selectedZones.get(region).isEmpty()
                        || selectedZones.get(region).contains(zone)));
    }

    public ZoneSelection add(String region, List<String> zones) {
        selectedZones.computeIfAbsent(region, k -> new ArrayList<>()).addAll(zones);
        return this;
    }

    public ZoneSelection add(String region) {
        if ("ALL".equals(region)) {
            selectAll();
        } else {
            selectedZones.put(region, new ArrayList<>());
        }
        return this;
    }

    public ZoneSelection selectAll() {
        allSelected = true;
        return this;
    }
}
