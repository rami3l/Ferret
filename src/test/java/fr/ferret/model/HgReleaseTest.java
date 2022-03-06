package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class HgReleaseTest {

    @Test
    void testOfWorks() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBldr = dbf.newDocumentBuilder();
            org.w3c.dom.Document xmlDocument =
                    docBldr.parse("src.test.resources.hgReleaseTestFonctionne.xml");
            Node node = xmlDocument.getDocumentElement();
            var hgRelease = HgRelease.of(node).get();
            assertEquals("GRCh38", hgRelease.getHgVersion());
            assertEquals(13, hgRelease.getPatch());
            assertEquals(39, hgRelease.getAssVersion());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }
}
