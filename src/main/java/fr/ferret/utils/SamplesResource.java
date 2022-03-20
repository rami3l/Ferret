package fr.ferret.utils;

import fr.ferret.controller.exceptions.ExceptionHandler;
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

    /**
     * Loads the information about the phases from the phaseList file
     */
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
        ).orElseGet(ExceptionHandler::phaseInitialisationError);
    }

    /**
     * Tries to load the sample (people, zones and regions) for the given phase. Returns an empty
     * Map in case of failure (for example if the given phase is unknown in the phaseList file)
     */
    private Map<String, Map<String, Set<String>>> tryLoadSample(Phase1KG phase) {
        var phaseFile = phases.get(phase);
        if(phaseFile == null || phaseFile.isBlank())
        {
            logger.log(Level.WARNING, "No phase file for selected phase {0}", phase);
            ExceptionHandler.phaseNotFoundError();
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

    /**
     * Returns the sample (people, zones and regions) for the given phase. Returns an empty
     * Map if it failed to load (for example if the given phase is unknown in the phaseList file)
     */
    public Map<String, Map<String, Set<String>>> getSample(Phase1KG phase) {
        return samples.computeIfAbsent(phase, SamplesResource::tryLoadSample);
    }

}
