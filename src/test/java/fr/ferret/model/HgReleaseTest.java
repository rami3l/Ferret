package fr.ferret.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import fr.ferret.model.hgversion.HgRelease;
import org.junit.jupiter.api.BeforeAll;
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
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        xmlDocument = docBuilder.parse("src/test/resources/hgReleaseNode.xml");
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


    /**
     * This test is commented because it is not a real unit test (it reaches the server, and even
     * worse, the values 25 and 39 may change over the time). It is here only to be able to test the
     * update functionality when needed.
     */

    /*
    @Test
    void testAssemblyAccessVersionsUpdate() {
        var theMap = HgVersion.getLatestAssemblyAccessVersions(
            Arrays.asList(HumanGenomeVersions.HG19, HumanGenomeVersions.HG38));
        var hg37 = theMap.get(HumanGenomeVersions.HG19);
        var hg38 = theMap.get(HumanGenomeVersions.HG38);
        assertTrue(hg37.isPresent());
        assertTrue(hg38.isPresent());
        assertEquals(25, hg37.get());
        assertEquals(39, hg38.get());
    }
    */
}
