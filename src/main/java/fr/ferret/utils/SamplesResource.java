package fr.ferret.utils;

import fr.ferret.model.Phase1KG;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@UtilityClass
class SamplesResource {

    private static final Logger logger = Logger.getLogger(SamplesResource.class.getName());

    private final String SAMPLES_DIR = "samples/";
    private final String PHASES_FILE = SAMPLES_DIR + "phaseList.txt";

    @Getter
    private final Map<Phase1KG, String> phases = loadPhases();
    private final Map<Phase1KG, Map<String, Map<String, Set<String>>>> samples = new HashMap<>();

    private Map<Phase1KG, String> loadPhases() {
        return ResourceFile.readResource(PHASES_FILE, reader ->
            reader.lines().filter(Predicate.not(String::isBlank)).map(line -> line.split("\\s*:\\s*"))
                .collect(Collectors.toMap(
                    line -> new Phase1KG(line[0], line[1]),
                    line -> line.length > 2 ? line[2] : "",
                    (x, y) -> y,
                    // we use a LinkedHashMap to preserve the order of the phases
                    LinkedHashMap::new
                ))
        ).orElseThrow();
    }

    private Map<String, Map<String, Set<String>>> tryLoadSample(Phase1KG phase) {
        var phaseFile = phases.get(phase);
        if(phaseFile == null || phaseFile.isBlank())
        {
            logger.log(Level.WARNING, "No phase file for selected phase {0}", phase);
            return Collections.emptyMap();
        }
        return ResourceFile.readResource(SAMPLES_DIR + phaseFile, reader ->
            reader.lines().map(line -> line.split("\t")).collect(
                Collectors.groupingBy(
                    line -> line[2],
                    Collectors.groupingBy(
                        line -> line[1],
                        Collectors.mapping(
                            line -> line[0],
                            Collectors.toSet()
                        )
                    )
                )
            )).orElse(Collections.emptyMap());
    }

    public Map<String, Map<String, Set<String>>> getSample(Phase1KG phase) {
        return samples.computeIfAbsent(phase, SamplesResource::tryLoadSample);
    }

}
