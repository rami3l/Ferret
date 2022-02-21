package fr.ferret.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocusTest {

    @Test void from() {
        var locus = Locus.from(List.of("1234", "1235"));
        locus.forEach(System.out::println);
    }
}
