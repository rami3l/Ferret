package fr.ferret.model.hgversion;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fr.ferret.model.utils.XmlParser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fr.ferret.controller.settings.HumanGenomeVersions;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HgVersionUpdater {

        // TODO: move to the resources
        private static final String URLTEST =
                        "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=gene&id=1234&retmode=xml";

        /**
         * @param currentGNode general node corresponding to an idGene
         * @return Map<HumanGenomeVersion> : key = version ; value = accessionVersion
         */
        public Optional<Map<HumanGenomeVersions, Integer>> getPatchesFromVersions(
                        List<HumanGenomeVersions> versionList) {
                org.w3c.dom.Document xmlDocument = XmlParser.document(URLTEST);
                Node gNode = xmlDocument.getDocumentElement();
                return getXmlReleasesNode(gNode).map(
                                locationHistory -> extractPatches(locationHistory, versionList));
        }

        /**
         * @param currentGNode general node corresponding to an idGene
         * @return Node : contains the nodes that have the information of the HgReleases
         */
        private Optional<Node> getXmlReleasesNode(Node currentGNode) {
                // go down on the path :
                return XmlParser.getNodeFromPath(currentGNode,
                                Arrays.asList("Entrezgene", "Entrezgene_comments"))
                                .map(nodeFromPath -> XmlParser.xmlCommentFinder(nodeFromPath, "254",
                                                "Gene Location History",
                                                "Gene-commentary_comment"));

        }

        /**
         * @param geneCommentaryComments
         * @param versionList
         * @return Node : has the highest release then date corresponding to the good version
         */
        private Map<HumanGenomeVersions, Integer> extractPatches(Node geneCommentaryComments,
                        List<HumanGenomeVersions> versionList) {

                NodeList annotationReleases = geneCommentaryComments.getChildNodes();
                var releaseList = IntStream.range(0, annotationReleases.getLength())
                                .mapToObj(annotationReleases::item).map(HgRelease::of)
                                .filter(Optional::isPresent).map(Optional::get).distinct().toList();

                return versionList.stream().collect(Collectors.toMap(Function.identity(),
                                version -> releaseList.stream()
                                                .filter(release -> version.toGRC()
                                                                .equals(release.getHgVersion()))
                                                .max(Comparator.comparing(HgRelease::getPatch))
                                                .get().getAssVersion()));
        }
}
