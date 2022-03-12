package fr.ferret.model.hgversion;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fr.ferret.model.utils.XmlParser;
import fr.ferret.utils.Resource;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fr.ferret.controller.settings.HumanGenomeVersions;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HgVersion {

    private static final String XML_DOC_URL = Resource.getServerConfig("ncbi.assAacVersion.url");
    private static final String HG_RELEASES_PATH = Resource.getServerConfig("ncbi.assAacVersion.xmlPath");


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
            // NodeList to Stream of nodes conversion
            IntStream.range(0, annotationReleases.getLength()).mapToObj(annotationReleases::item)
                // Create HgRelease objects from XML nodes, getting only distinct ones
                .map(HgRelease::of).flatMap(Optional::stream).distinct().toList();

        // Creation of a map with HG versions as keys...
        return versions.stream().collect(Collectors.toMap(Function.identity(),
            // And the max assembly accession versions for each HG version as values
            version -> releaseList.stream()
                .filter(release -> version.toGRC().equals(release.getHgVersion()))
                .max(Comparator.comparing(HgRelease::getPatch)).map(HgRelease::getAssVersion)));
    }
}
