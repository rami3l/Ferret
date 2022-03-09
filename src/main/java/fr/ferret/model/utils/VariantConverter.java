package fr.ferret.model.utils;

import fr.ferret.model.locus.Locus;
import reactor.core.publisher.Mono;

public class VariantConverter {
    // TODO: create json path

    public Mono<Locus> extractLocus(JsonDocument json) {
        // TODO: extract the locus from the json
        return Mono.empty();
    }
}
