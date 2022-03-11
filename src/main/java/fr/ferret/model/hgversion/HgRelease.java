package fr.ferret.model.hgversion;

import java.util.Arrays;
import java.util.Optional;

import fr.ferret.model.utils.XmlParser;
import org.w3c.dom.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Allow to have the hgVersion and patch (ex : "GRCh38" and 13 for "GRCh38.p13") and the accession
 * version (ex : 39 for "GCF_000001405.39")
 */
@Getter
@EqualsAndHashCode
public class HgRelease {
    private final String hgVersion;
    private final int patch;
    private final int assVersion;


    private HgRelease(String hgVersion, int patch, int assVersion) {
        this.hgVersion = hgVersion;
        this.patch = patch;
        this.assVersion = assVersion;
    }

    /**
     * @param node which may contains a release
     * @return Optional<HgRelease> : exists if found ; Optional.empty() if not
     */
    public static Optional<HgRelease> of(Node node) {
        Optional<Node> preciseNode =
                XmlParser.getNodeByPath(node,"Gene-commentary_comment/Gene-commentary");
        var versionOpt = preciseNode
                .flatMap(precise -> XmlParser.getNodeByPath(precise, "Gene-commentary_heading"))
                .flatMap(headingNode -> Optional.ofNullable(headingNode.getFirstChild()))
                .map(lastNode -> lastNode.getNodeValue().split("\\.p"));

        var assVersionOpt = preciseNode
                .flatMap(precise -> XmlParser.getNodeByPath(precise, "Gene-commentary_version"))
                .flatMap(headingNode -> Optional.ofNullable(headingNode.getFirstChild()))
                .map(lastNode -> Integer.parseInt(lastNode.getNodeValue()));

        return versionOpt.flatMap(v -> assVersionOpt
                .map(assVersion -> new HgRelease(v[0], Integer.parseInt(v[1]), assVersion)));

    }
}
