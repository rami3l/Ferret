package fr.ferret.model;

import java.util.ArrayList;
import java.util.List;
import fr.ferret.controller.settings.HumanGenomeVersions;
import lombok.var;

public class GeneToLocusTest {
    public static void main(String[] args) {
        var currentTime = System.currentTimeMillis();
        var idList = List.of("KCNT2", "343450", "CCR5", "1234", "MICB", "4277", "IL6", "3569",
                "APOL1", "8542", "4627", "MYH9");
        GeneToId.geneListToID(idList);
        System.out.println(idList);
        var geneToLocusV = new GeneToLocus(HumanGenomeVersions.hg38);
        System.out.println(geneToLocusV.idListToLocus(idList));
        System.out.println(System.currentTimeMillis() - currentTime);
    }
}
