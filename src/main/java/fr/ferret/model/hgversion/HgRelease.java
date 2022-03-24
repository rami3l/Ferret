package fr.ferret.model.hgversion;

import fr.ferret.model.utils.XmlParser;
import fr.ferret.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.w3c.dom.Node;

import java.util.Optional;

/**
 * Represents a Human Genome Version.
 * It contains the hgVersion and the patch (ex : "GRCh38" and 13 for "GRCh38.p13")
 * and the assembly accession version (ex : 39 for "GCF_000001405.39")
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class HgRelease {

    private static final String HG_RELEASE_PATH = "Gene-commentary_comment/Gene-commentary";
    public static final String HG_VERSION_TAG = "Gene-commentary_heading";
    public static final String ASS_ACC_VERSION_TAG = "Gene-commentary_version";

    private final String hgVersion;
    private final int patch;
    private final int assVersion;

    /**
     * Extracts information to create an {@link HgRelease} from an XML {@link Node} which has this
     * structure:
     * <pre>{@code
     * <Gene-commentary>
     *     ...
     *     <Gene-commentary_comment>
     *         <Gene-commentary>
     *             <Gene-commentary_type value="assembly">24</Gene-commentary_type>
     *             <Gene-commentary_heading>GRCh38.p13</Gene-commentary_heading>
     *             <Gene-commentary_label>Reference</Gene-commentary_label>
     *             <Gene-commentary_accession>GCF_000001405</Gene-commentary_accession>
     *             <Gene-commentary_version>39</Gene-commentary_version>'''
     *     ...
     * </Gene-commentary>
     * }</pre>
     *
     * @param hgReleaseNode An XML {@link Node} which may contains a release
     * @return an {@link Optional} {@link HgRelease} empty if no HgRelease found in the {@link Node}
     */
    public static Optional<HgRelease> of(Node hgReleaseNode) {
        Optional<Node> hgVersionNode =
                XmlParser.getNodeByPath(hgReleaseNode, HG_RELEASE_PATH);
        var versionOpt = hgVersionNode
                .flatMap(node -> XmlParser.getNodeByPath(node, HG_VERSION_TAG))
                .flatMap(headingNode -> Optional.ofNullable(headingNode.getFirstChild()))
                .map(lastNode -> lastNode.getNodeValue().split("\\.p"));

        var assVersionOpt = hgVersionNode
                .flatMap(precise -> XmlParser.getNodeByPath(precise, ASS_ACC_VERSION_TAG))
                .flatMap(headingNode -> Optional.ofNullable(headingNode.getFirstChild()))
                .map(lastNode -> Integer.parseInt(lastNode.getNodeValue()));

        return versionOpt.flatMap(v -> assVersionOpt
                .map(assVersion -> new HgRelease(v[0], Integer.parseInt(v[1]), assVersion)));
    }

    public static HgRelease from(String hgVersion) {
        var versionAndPatch = hgVersion.split("\\.p");
        var version = versionAndPatch[0];
        int patch = 0;
        if(versionAndPatch.length > 1 && Utils.isInteger(versionAndPatch[1])) {
            patch = Integer.parseInt(versionAndPatch[1]);
        }
        return new HgRelease(version, patch, -1);
    }
}
