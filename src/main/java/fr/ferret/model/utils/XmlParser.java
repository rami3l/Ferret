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


    /**
     * Gets an {@link Optional} {@link Node} from a parent XML {@link Node} using the path parameter
     *
     * @param parent The parent {@link Node} to start the search from
     * @param path The rules used to search the {@link Node}. See <a href="https://docs.oracle.com/
     *             javase/7/docs/api/javax/xml/xpath/package-summary.html">XPath Expressions</a>
     *             for more information
     * @return An {@link Optional} {@link Node}, empty if no {@link Node} found
     */
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
     * @param xmlURI The URI of the XML document to parse
     * @return An {@link Optional} {@link Document}, empty if an error occurred during parsing
     */
    public static Optional<Document> parse(String xmlURI) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);
            // Access to external entities disabled to avoid XXE vulnerability
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            return Optional.of(docBuilder.parse(xmlURI));
        } catch (ParserConfigurationException | SAXException | IOException | RuntimeException e) {
            // TODO: call ExceptionHandler to show a popup
            logger.log(Level.WARNING, "Failed to parse XML", e);
            return Optional.empty();
        }
    }

}
