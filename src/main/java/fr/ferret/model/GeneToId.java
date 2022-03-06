package fr.ferret.model;

import java.util.List;
import org.w3c.dom.Node;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class GeneToId {
    public static final String URLNAME =
            "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term=";
    public static final String ENDURLNAME = "[GENE]%20AND%20human[ORGN]&retmode=xml";

    private GeneToId() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Transform the gene’s names to ids
     * 
     * @param nameList : list to transform
     */
    public static void geneListToID(List<String> nameList) {
        for (int i = 0; i < nameList.size(); i++) {
            String nameOrId = nameList.get(i);
            if (!XmlParse.isNumeric(nameOrId)) {
                nameList.set(i, nameToId(nameOrId));
            }
        }
    }


    /**
     * Transform a name of gene into an id
     * 
     * @param name of gene
     * @return String : id of the gene name
     */
    private static String nameToId(String name) {
        String xmlGeneURL = getURLFromName(name);
        org.w3c.dom.Document xmlDocument = XmlParse.document(xmlGeneURL);
        Node origin = xmlDocument.getDocumentElement();
        try {
            return XmlParse.getNodeFromPath(origin, List.of("IdList", "Id")).getFirstChild()
                    .getNodeValue();
        } catch (NullPointerException e) {
            System.out.println("Le nom d’id est");
            return null;
        }

    }

    /**
     * @param name : name of the gene
     * @return String : URL
     */
    public static String getURLFromName(String name) {
        return URLNAME + name + ENDURLNAME;
    }
}
