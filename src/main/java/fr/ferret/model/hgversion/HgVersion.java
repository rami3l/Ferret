package fr.ferret.model.hgversion;

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
public class HgVersion {

    // TODO: move to the resources
    private static final String XML_DOC_URL =
        "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=gene&id=1234&retmode=xml";

    private static final String HG_RELEASES_PATH =
        "/Entrezgene-Set/Entrezgene/Entrezgene_comments/Gene-commentary[Gene-commentary_heading/text() = 'Gene Location History']/Gene-commentary_comment";

    /**
     * @param versions The {@link List} of {@link HumanGenomeVersions} to get the assembly accession
     *                 versions for
     * @return A {@link Map} containing an {@link Optional} {@link Integer} corresponding to the
     * latest assembly accession version, for each HG version passed in parameter
     */
    public Map<HumanGenomeVersions, Optional<Integer>> getLatestAssemblyAccessVersions(
        List<HumanGenomeVersions> versions) {
        var releases = XmlParser.parse(XML_DOC_URL)
            .flatMap(document -> XmlParser.getNodeByPath(document, HG_RELEASES_PATH));
        return releases.map(locationHistory -> extractAssAccVersions(locationHistory, versions))
            .orElse(versions.stream()
                .collect(Collectors.toMap(Function.identity(), v -> Optional.empty())));
    }

    /**
     * @param locationHistoryNode The XML {@link Node} which contains the historic locations of the
     *                            gene, and by the way all the HG releases
     * @param versions            The HG versions to get the assembly accession version for
     * @return The {@link Map} containing an {@link Optional} assembly accession version for each
     * HG version passed in parameter
     */
    private Map<HumanGenomeVersions, Optional<Integer>> extractAssAccVersions(
        Node locationHistoryNode, List<HumanGenomeVersions> versions) {

        NodeList annotationReleases = locationHistoryNode.getChildNodes();
        var releaseList =
            IntStream.range(0, annotationReleases.getLength()).mapToObj(annotationReleases::item)
                .map(HgRelease::of).flatMap(Optional::stream).distinct().toList();

        return versions.stream().collect(Collectors.toMap(Function.identity(),
            version -> releaseList.stream()
                .filter(release -> version.toGRC().equals(release.getHgVersion()))
                .max(Comparator.comparing(HgRelease::getPatch)).map(HgRelease::getAssVersion)));
    }
}
