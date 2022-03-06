package fr.ferret.model;

import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParse {

    private XmlParse() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @param parentNode
     * @param childName
     * @return Node : first child of the parentNode named childName
     */
    public static Node getNodeFromPath(Node parentNode, String childName) {
        NodeList childrenNodeList = parentNode.getChildNodes();
        int listLength = childrenNodeList.getLength();
        for (int i = 0; i < listLength; i++) { // Search into all children of parentNode
            if (childrenNodeList.item(i).getNodeName().equals(childName)) { // We select the first
                                                                            // one corresponding
                return childrenNodeList.item(i);
            }
        }
        return null;
    }


    /**
     * @param parentNode
     * @param childNames
     * @return Node : child leaded by the path. the first childname of the list is the direct child
     *         of the parentNode
     */
    public static Node getNodeFromPath(Node parentNode, List<String> childNames) {
        if (childNames.size() == 1) { // There is only one child
            return getNodeFromPath(parentNode, childNames.get(0));
        } else { // We get the first child named after childNames.get(0) and we go again with the
                 // same list without the first childName
            return getNodeFromPath(getNodeFromPath(parentNode, childNames.get(0)),
                    childNames.subList(1, childNames.size()));
        }
    }


    /**
     * Each comment has a commentary type and commentary heading. Given a parentNode of comment
     * nodes, this will search through the comments finding the one with matching type and matching
     * heading. It will return the specified node.
     * 
     * @param test
     * @param typeDesired
     * @param headingDesired
     * @param nodeNameToRetrieve
     * @return Node
     */
    public static Node xmlCommentFinder(Node parentNode, String typeDesired, String headingDesired,
            String nodeNameToRetrieve) {
        NodeList test = parentNode.getChildNodes();
        Node toReturn = null;
        int listLength = test.getLength();
        Node desiredNode = null;
        for (int i = 0; i < listLength; i++) {
            Node currentNode = test.item(i);
            if (xmlCommentChecker(currentNode, typeDesired, headingDesired)) {
                desiredNode = currentNode;
                break;
            }
        }

        if (desiredNode != null) {
            NodeList desiredNodeList = desiredNode.getChildNodes();
            listLength = desiredNodeList.getLength();
            for (int i = 0; i < listLength; i++) {
                if (desiredNodeList.item(i).getNodeName().equals(nodeNameToRetrieve)) {
                    toReturn = desiredNodeList.item(i);
                }
            }
        }

        return toReturn;
    }


    /**
     * taken from ferret v2
     * 
     * @param test
     * @param typeDesired
     * @param headingDesired
     * @return boolean : Check if the node tested has the type desired and the heading desired
     */
    private static boolean xmlCommentChecker(Node test, String typeDesired, String headingDesired) {
        boolean typeMatch = false;
        boolean headingMatch = false;
        // If we get "any", we don’t check the corresponding type/heading
        if (typeDesired.equals("any")) {
            typeMatch = true;
        }
        if (headingDesired.equals("any")) {
            headingMatch = true;
        }
        NodeList commentTagList = test.getChildNodes();
        int commentLength = commentTagList.getLength();
        for (int j = 0; j < commentLength; j++) {
            String commentString = commentTagList.item(j).getNodeName();
            switch (commentString) {
                case "Gene-commentary_type":
                    NodeList geneCommentaryType = commentTagList.item(j).getChildNodes();
                    for (int k = 0; k < geneCommentaryType.getLength(); k++) {
                        if (geneCommentaryType.item(k).getNodeType() == Node.TEXT_NODE) {
                            typeMatch =
                                    geneCommentaryType.item(k).getNodeValue().equals(typeDesired);
                        }
                    }
                    break;
                case "Gene-commentary_heading":
                    NodeList geneCommentaryHeading = commentTagList.item(j).getChildNodes();
                    for (int k = 0; k < geneCommentaryHeading.getLength(); k++) {
                        if (geneCommentaryHeading.item(k).getNodeType() == Node.TEXT_NODE) {
                            headingMatch = geneCommentaryHeading.item(k).getNodeValue()
                                    .equals(headingDesired);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return typeMatch && headingMatch;
    }


    /**
     * @param xmlGeneURL
     * @return Document
     */
    public static Document document(String xmlGeneURL) {
        DocumentBuilder docBldr;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setCoalescing(false);
        try {
            docBldr = dbf.newDocumentBuilder();
            pause(400);
            return docBldr.parse(xmlGeneURL);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("Parsing du XML donnant les locus à partir des ids de gène échoué");
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param time of pause
     */
    private static void pause(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param strNum
     * @return boolean : True if strNum is an integer
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
