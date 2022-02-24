package fr.ferret.model;

import java.util.List;
import org.w3c.dom.Node;

public class GeneToId {

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
        String xmlGeneURL = XmlParse.getURLFromName(name);
        org.w3c.dom.Document xmlDocument = XmlParse.document(xmlGeneURL);
        Node origin = xmlDocument.getDocumentElement();
        return XmlParse.getNodeFromPath(origin, List.of("IdList", "Id")).getFirstChild()
                .getNodeValue();

    }
}
