package fr.ferret.model;

import java.util.Optional;
import org.w3c.dom.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Allow to have the release and the date of a specified node
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

    public static Optional<HgRelease> of(Node node) {
        String[] version = XmlParse.getNodeFromPath(node, "Gene-commentary_heading").getFirstChild()
                .getNodeValue().split("\\.p");
        try {
            int patch = Integer.parseInt(version[1]);
            String hgVersion = version[0];
            int assVersion =
                    Integer.parseInt(XmlParse.getNodeFromPath(node, "Gene-commentary_accession")
                            .getFirstChild().getNodeValue());
            return Optional.of(new HgRelease(hgVersion, patch, assVersion));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
