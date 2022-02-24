package fr.ferret.model;

import java.util.ArrayList;
import java.util.List;
import fr.ferret.controller.settings.HumanGenomeVersions;
import lombok.var;

public class GeneToLocusTest {
    public static void main(String[] args) {
        var currentTime = System.currentTimeMillis();
        var idList = new ArrayList<String>();
        idList.add("KCNT2");
        idList.add("343450");
        idList.add("CCR5");
        idList.add("1234");
        idList.add("MICB");
        idList.add("4277");
        idList.add("IL6");
        idList.add("3569");
        idList.add("APOL1");
        idList.add("8542");
        idList.add("4627");
        idList.add("MYH9");

        GeneToId.GeneListToID(idList);
        System.out.println(idList);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        var geneToLocusV = new GeneToLocus(HumanGenomeVersions.hg38);
        System.out.println(geneToLocusV.idListToLocus((List) idList));
        System.out.println(System.currentTimeMillis() - currentTime);
    }
}
