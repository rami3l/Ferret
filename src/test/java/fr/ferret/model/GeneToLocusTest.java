package fr.ferret.model;

import java.util.ArrayList;
import java.util.List;
import fr.ferret.controller.settings.HumanGenomeVersions;
import lombok.var;

public class GeneToLocusTest {
    public static void main(String[] args) {
        var currentTime = System.currentTimeMillis();
        var idList = new ArrayList<>();
        idList.add("343450");
        idList.add("1234");
        idList.add("4277");
        idList.add("3569");
        idList.add("8542");
        idList.add("4627");
        var geneToLocusV = new GeneToLocus(HumanGenomeVersions.hg38);
        System.out.println(geneToLocusV.idToLocus((List) idList));
        System.out.println(System.currentTimeMillis() - currentTime);
    }

}
