package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import fr.ferret.controller.settings.HumanGenomeVersions;

public class HgReleaseTest {

    public static void main(String[] args) {
        var theMap = VersionUpdater.getPatchesFromVersions(
                Arrays.asList(HumanGenomeVersions.HG19, HumanGenomeVersions.HG38));
        System.out.println(theMap.toString());;
    }

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
