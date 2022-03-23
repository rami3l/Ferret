package fr.ferret.model.utils;

import fr.ferret.TestUtils;
import fr.ferret.utils.Resource;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneConverterTest {

    @Test
    void getLocusFromFile_ShouldReturnLocus() {
        // ARRANGE
        var id = "1234"; // we need the id to extract it because a json can contain multiple ids
        var assemblyAccVer = "GCF_000001405.39";
        // https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=gene&id=1234&format=json
        var json = new JsonDocument(TestUtils.getContent("ncbi/gene-id-to-locus/1234.json"));

        // ACT
        var locus = GeneConverter.extractLocus(id, assemblyAccVer, json).blockOptional();

        // ASSERT
        assertTrue(locus.isPresent());
        assertEquals("3", locus.get().getChromosome());
        assertEquals(46370141, locus.get().getStart());
        assertEquals(46376205, locus.get().getEnd());
    }

    @Test
    void getLocusFromUnknownOrDiscontinuedId_ShouldReturnEmpty() {
        // ARRANGE
        var id1 = "123456"; // Discontinued
        var id2 = "0"; // Unknown
        var assemblyAccVer = "GCF_000001405.39";
        // https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=gene&id=123456&format=json
        var json1 = new JsonDocument(TestUtils.getContent("ncbi/gene-id-to-locus/123456.json"));
        // https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=gene&id=0&format=json
        var json2 = new JsonDocument(TestUtils.getContent("ncbi/gene-id-to-locus/0.json"));

        // ACT
        var locus1 = GeneConverter.extractLocus(id1, assemblyAccVer, json1).blockOptional();
        var locus2 = GeneConverter.extractLocus(id2, assemblyAccVer, json2).blockOptional();

        // ASSERT
        assertTrue(locus1.isEmpty());
        assertTrue(locus2.isEmpty());
    }

    @Test
    void getIdFromFile_ShouldReturnGeneId() {
        // ARRANGE
        // https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term=CR5[GENE]%20AND%20human[ORGN]&retmode=json
        var json = new JsonDocument(TestUtils.getContent("ncbi/gene-name-to-id/CR5.json"));

        // ACT
        var id = GeneConverter.extractId(json).blockOptional();

        // ASSERT
        assertTrue(id.isPresent());
        assertEquals("22814", id.get());
    }

    @Test
    void getIdFromUnknownName_ShouldReturnEmpty() {
        // ARRANGE
        // https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term=unknown[GENE]%20AND%20human[ORGN]&retmode=json
        var json = new JsonDocument(TestUtils.getContent("ncbi/gene-name-to-id/unknown.json"));

        // ACT
        var id = GeneConverter.extractId(json).blockOptional();

        // ASSERT
        assertTrue(id.isEmpty());
    }

}
