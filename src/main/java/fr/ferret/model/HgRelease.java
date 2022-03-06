package fr.ferret.model;

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
        String[] version = XmlParser.getNodeFromPath(node, "Gene-commentary_heading").getFirstChild()
                .getNodeValue().split("\\.p");
        try {
            int patch = Integer.parseInt(version[1]);
            String hgVersion = version[0];
            int assVersion =
                    Integer.parseInt(XmlParser.getNodeFromPath(node, "Gene-commentary_accession")
                            .getFirstChild().getNodeValue());
            return Optional.of(new HgRelease(hgVersion, patch, assVersion));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
