package fr.ferret.model.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import lombok.experimental.UtilityClass;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@UtilityClass
public class XmlParser {

    private static final Logger logger = Logger.getLogger(XmlParser.class.getName());

    private static final XPath xPath = XPathFactory.newInstance().newXPath();


    public static Optional<Node> getNodeByPath(Node parent, String path) {
        try {
            var nodeList = (NodeList) xPath.compile(path).evaluate(parent, XPathConstants.NODESET);
            return Optional.ofNullable(nodeList.item(0));
        } catch (Exception e) {
            // TODO: ExceptionHandler
            return Optional.empty();
        }
    }


    /**
     * @param parentNode The {@link Node} to search a child node of
     * @param childName The name of the child {@link Node} searched
     * @return The first child {@link Node} (optional) named childName
     */
    public static Optional<Node> getChildByName(Node parentNode, String childName) {
        NodeList childrenNodeList = parentNode.getChildNodes();
        return IntStream.range(0, childrenNodeList.getLength()).mapToObj(childrenNodeList::item)
            .filter(node -> node.getNodeName().equals(childName)).findFirst();
    }

    /**
     * @param parentNode
     * @param childNames
     * @return Node : child leaded by the path. the first childname of the list is the direct child
     *         of the parentNode
     */
    public static Optional<Node> getNodeFromPath(Node parentNode, List<String> childNames) {
        if (childNames.isEmpty()) { // There is no child
            return Optional.of(parentNode);
        } else { // We get the first child named after childNames.get(0) and we go again with the
                 // same list without the first childName
            return getChildByName(parentNode, childNames.get(0))
                .flatMap(child -> getNodeFromPath(child, childNames.subList(1, childNames.size())));
        }
    }


    /**
     * @param xmlGeneURL
     * @return Document
     */
    public static Document document(String xmlGeneURL) {
        DocumentBuilder docBldr;
        // TODO: disable access to external entities (cf XXE)
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setCoalescing(false);
        try {
            docBldr = dbf.newDocumentBuilder();
            return docBldr.parse(xmlGeneURL);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO: call ExceptionHandler to show a popup
            logger.log(Level.WARNING, "Parsing du XML donnant les locus à partir des ids de gène échoué", e);
            return null;
        }
    }

}
