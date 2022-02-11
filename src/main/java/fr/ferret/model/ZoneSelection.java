package fr.ferret.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZoneSelection {

    /**
     * The keys represent the selected regions and the values represent the zones selected
     * in each region. A region of which all zones are selected can be represented by an
     * empty list for its value.
     */
    private final Map<String, List<String>> selectedZones = new HashMap<>();

    public boolean isSelected(String region, String zone) {
        return selectedZones.containsKey(region) &&
                (selectedZones.get(region).isEmpty() || selectedZones.get(region).contains(zone));
    }

    public void add(String region, List<String> zones) {
        selectedZones.computeIfAbsent(region, k -> new ArrayList<>()).addAll(zones);
    }

    public void add(String region) {
        selectedZones.put(region, new ArrayList<>());
    }
}
