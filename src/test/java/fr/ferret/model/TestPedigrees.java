package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import fr.ferret.model.conversions.GenotypePair;
import fr.ferret.model.conversions.PedRecord;
import fr.ferret.model.conversions.Pedigree;
import fr.ferret.utils.Resource;

class TestPedigrees {
    @Test
    void testGetPedigrees() throws IOException {
        // Family ID Individual ID Paternal ID Maternal ID Gender Phenotype Population Relationship
        // Siblings Second Order Third Order Children Other Comments
        // GBR003 HG00153 0 HG00158 2 0 GBR child 0 0 0 0 0
        var pedigrees = Resource.getPedigrees();
        var got = pedigrees.get("HG00153");
        // Male("M", 1), Female("F", 2), Unknown("U",-9), NotReported("N",-9);
        var expected =
                Pedigree.builder().familyId("GBR003").individualId("HG00153").maternalId("HG00158")
                        .gender(/* Female */ 2).population("GBR").relationship("child").build();
        assertEquals(expected, got);
    }

    @Test
    void testWritePedigrees() throws IOException {
        var pedigree =
                Pedigree.builder().familyId("GBR003").individualId("HG00153").maternalId("HG00158")
                        .gender(/* Female */ 2).population("GBR").relationship("child").build();
        var variants = List.of(new GenotypePair("T", "T"), new GenotypePair("A", "G"));
        var pedRecord = new PedRecord(pedigree, variants);
        var got = pedRecord.toString();
        var expected = "GBR003\tHG00153\t0\tHG00158\t2\t0\tT\tT\tA\tG";
        assertEquals(expected, got);
    }
}
