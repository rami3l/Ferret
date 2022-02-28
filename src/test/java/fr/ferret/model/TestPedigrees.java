package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import fr.ferret.model.conversions.Pedigree;
import fr.ferret.utils.Resource;
import picard.pedigree.Sex;

class TestPedigrees {
    @Test
    void testGetPedigrees() throws IOException {
        // Family ID Individual ID Paternal ID Maternal ID Gender Phenotype Population Relationship
        // Siblings Second Order Third Order Children Other Comments
        // GBR003 HG00153 0 HG00158 2 0 GBR child 0 0 0 0 0
        var pedigrees = Resource.getPedigrees();
        var got = pedigrees.get("HG00153");

        var expected = Pedigree.builder().familyId("GBR003").individualId("HG00153")
                .maternalId("HG00158").gender(Sex.Female.toCode()).population("GBR")
                .relationship("child").build();
        assertEquals(expected, got);
    }
}
