package fr.ferret.model;

import java.io.PrintStream;
import java.util.List;
import org.w3c.dom.Node;

public class GeneToId {

    private GeneToId() {
        throw new IllegalStateException("Utility class");
    }

    public static void GeneListToID(List<String> nameList) {
        for (int i = 0; i < nameList.size(); i++) {
            String nameOrId = nameList.get(i);
            if (!XmlParse.isNumeric(nameOrId)) {
                nameList.set(i, NameToId(nameOrId));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private static String NameToId(String nameOrId) {
        String xmlGeneURL = XmlParse.getURLFromName(nameOrId);
        org.w3c.dom.Document xmlDocument = XmlParse.document(xmlGeneURL);
        Node origin = xmlDocument.getDocumentElement();
        return XmlParse.getNodeFromPath(origin, List.of("IdList", "Id")).getFirstChild()
                .getNodeValue();

    }
}
