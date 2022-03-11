package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HgReleaseTest {

    Document xmlDocument;

    @BeforeAll
    void init() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBldr = dbf.newDocumentBuilder();
        xmlDocument = docBldr.parse("src/test/resources/hgReleaseNode.xml");
    }

    @Test
    void testHgRelease_shouldBeExtractedFromAValidHgReleaseNode() {
        Node node = xmlDocument.getDocumentElement();
        var hgReleaseOpt = HgRelease.of(node);
        assertTrue(hgReleaseOpt.isPresent());
        var hgRelease = hgReleaseOpt.get();
        assertEquals("GRCh38", hgRelease.getHgVersion());
        assertEquals(13, hgRelease.getPatch());
        assertEquals(39, hgRelease.getAssVersion());
    }

    @Test
    void testHgReleaseNotFound_ShouldReturnEmpty() {
        Node node = xmlDocument.getDocumentElement().getFirstChild();
        var hgRelease = HgRelease.of(node);
        assertTrue(hgRelease.isEmpty());
    }


    // This test must be changed because it reaches the server

    // @Test
    // void testURL() {
    // var theMap = VersionUpdater.getPatchesFromVersions(
    // Arrays.asList(HumanGenomeVersions.HG19, HumanGenomeVersions.HG38));
    // assertTrue(theMap.isPresent());
    // var hg37 = theMap.get().get(HumanGenomeVersions.HG19);
    // var hg38 = theMap.get().get(HumanGenomeVersions.HG38);
    // assertEquals(25, hg37);
    // assertEquals(39, hg38);
    // }
}
