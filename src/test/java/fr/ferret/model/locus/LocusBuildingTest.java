package fr.ferret.model.locus;

import fr.ferret.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocusBuildingTest {

    @BeforeAll
    void init() throws Exception {
        String idUrlTemplate = "ncbi/gene-id-to-locus/%s.json";
        String nameUrlTemplate = "ncbi/gene-name-to-id/%s.json";
        TestUtils.setFinalStatic(LocusBuilding.class, "ID_URL_TEMPLATE", idUrlTemplate);
        TestUtils.setFinalStatic(LocusBuilding.class, "NAME_URL_TEMPLATE", nameUrlTemplate);
    }


    @Test
    void testCasNominal() {
        var genes = List.of("CR5", "1234");
        var builder = new LocusBuilding("GCF_000001405.39");
        var locusFlux = builder.startWith(genes).doOnNext(System.out::println);

        StepVerifier.create(locusFlux)
            .expectNext(new Locus("3", 46370141, 46376205))
            .expectNext(new Locus("8", 54073092, 54073992))
            .verifyComplete();
    }

    @Test
    void testIdNotFound_ShouldBeIgnored() {
        var genes = List.of("CR5", "1234", "0");
        var builder = new LocusBuilding("GCF_000001405.39");
        var locusFlux = builder.startWith(genes).doOnNext(System.out::println);

        StepVerifier.create(locusFlux)
            .expectNext(new Locus("3", 46370141, 46376205))
            .expectNext(new Locus("8", 54073092, 54073992))
            .verifyComplete();
    }

    /** Test to get the delay to apply between request to avoid 429 response code from server */

    //@Test
    //void testFromNames() {
    //
    //    var delayToAdjust = Duration.ofMillis(200);
    //
    //    var names = List.of("KCNT2", "CCR5", "MICB", "IL6", "APOL1", "MYH9");
    //    var builder = new LocusBuilding("GCF_000001405.39");
    //    var flux = Flux.fromIterable(names).delayElements(delayToAdjust)
    //            .flatMap(builder::fromName).doOnNext(System.out::println);
    //
    //    flux.blockLast();
    //    flux.blockLast();
    //    flux.blockLast();
    //    flux.blockLast();
    //    flux.blockLast();
    //    flux.blockLast();
    //    flux.blockLast();
    //}
}
