package fr.ferret.model.utils;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
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
     * @param xmlGeneURL
     * @return Document
     */
    public static Optional<Document> parse(String xmlGeneURL) {
        DocumentBuilder docBldr;
        // TODO: disable access to external entities (cf XXE)
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setCoalescing(false);
        try {
            docBldr = dbf.newDocumentBuilder();
            return Optional.of(docBldr.parse(xmlGeneURL));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO: call ExceptionHandler to show a popup
            logger.log(Level.WARNING, "Parsing du XML donnant les locus à partir des ids de gène échoué", e);
            return Optional.empty();
        }
    }

}
