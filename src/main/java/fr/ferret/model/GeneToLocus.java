package fr.ferret.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fr.ferret.controller.settings.HumanGenomeVersions;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GeneToLocus {
        private HumanGenomeVersions version;

        public List<Locus> idToLocus(List<String> idsgene) {
                List<Locus> locusList = new ArrayList<>();
                String idsString = this.idsToString((ArrayList<String>) idsgene);
                String xmlGeneURL =
                                "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=gene&id="
                                                + idsString + "&retmode=xml";
                org.w3c.dom.Document xmldDocument = XmlParse.document(xmlGeneURL);
                NodeList gNodeList = xmldDocument.getElementsByTagName("Entrezgene");

                for (int i = 0; i < gNodeList.getLength(); i++) {
                        Node currentGNode = gNodeList.item(i);
                        locusList.add(locusById(currentGNode));
                }
                return locusList;
        }

        private Locus locusById(Node currentGNode) {
                String chromosome;
                int start;
                int stop;

                try {
                        chromosome = XmlParse.getChildByName(
                                        XmlParse.getChildByName(XmlParse.getChildByName(XmlParse
                                                        .getChildByName(XmlParse.getChildByName(
                                                                        currentGNode,
                                                                        "Entrezgene_source"),
                                                                        "BioSource"),
                                                        "BioSource_subtype"), "SubSource"),
                                        "SubSource_name").getFirstChild().getNodeValue();
                        Node positionsNode = findPosition(currentGNode);
                        start = Integer.parseInt(
                                        XmlParse.getChildByName(positionsNode, "Seq-interval_from")
                                                        .getFirstChild().getNodeValue());
                        stop = Integer.parseInt(
                                        XmlParse.getChildByName(positionsNode, "Seq-interval_to")
                                                        .getFirstChild().getNodeValue());
                        return new Locus(chromosome, start, stop);
                } catch (NullPointerException e) {
                        System.out.println("Noeud" + e.getMessage()
                                        + "non trouvé dans l’arborescence");
                        e.printStackTrace();
                        return null;
                }
        }

        private Node findPosition(Node currentGNode) {
                Node geneLocationHistoryNode = XmlParse.xmlCommentFinder(
                                XmlParse.getChildByName(currentGNode, "Entrezgene_comments")
                                                .getChildNodes(),
                                "254", "Gene Location History", "Gene-commentary_comment");
                Node versioNode = findVersionNode(geneLocationHistoryNode);

                return XmlParse.getChildByName(XmlParse.getChildByName(
                                XmlParse.getChildByName(XmlParse.xmlCommentFinder(XmlParse
                                                .xmlCommentFinder(versioNode.getChildNodes(), "25",
                                                                "Primary Assembly",
                                                                "Gene-commentary_comment")
                                                .getChildNodes(), "1", "any",
                                                "Gene-commentary_seqs"), "Seq-loc"),
                                "Seq-loc_int"), "Seq-interval");
        }



        private Node findVersionNode(Node geneLocationHistoryNode) {
                NodeList annotationReleases = geneLocationHistoryNode.getChildNodes();
                ArrayList<XmlRelease> possibleNodesList = new ArrayList<>();
                for (int i = 0; i < annotationReleases.getLength(); i++) {
                        possibleNodesList.add(new XmlRelease(annotationReleases.item(i)));

                }
                XmlRelease.clean(possibleNodesList);
                XmlRelease.classify(possibleNodesList);
                var nodeSelected = selectNode(possibleNodesList);
                if (nodeSelected.isPresent()) {
                        return nodeSelected.get();
                }
                return null;
        }

        public Optional<Node> selectNode(List<XmlRelease> possibleNodesList) {
                Optional<Node> theNode = Optional.empty();
                int i = 0;
                int length = possibleNodesList.size();
                while (theNode.isEmpty() && i < length) {
                        var thePossibleNode = XmlParse.getChildByName(
                                        XmlParse.getChildByName(possibleNodesList.get(i).getNode(),
                                                        "Gene-commentary_comment"),
                                        "Gene-commentary");
                        if (Objects.equals(XmlParse
                                        .getChildByName(thePossibleNode, "Gene-commentary_heading")
                                        .getNodeValue().substring(0, 6), this.version.toString())) {
                                theNode = Optional.of(XmlParse.getChildByName(thePossibleNode,
                                                "Gene-commentary_comment"));
                        }
                        i += 1;
                }
                return theNode;
        }

        private String idsToString(ArrayList<String> idsgene) {
                StringBuilder idsString = new StringBuilder();
                for (String id : idsgene) {
                        idsString.append(id + ",");
                }
                return idsString.substring(0, idsString.length() - 1);
        }
}
