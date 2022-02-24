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
        private static final int OFFSET = 1; // result of a systematic error of the server when it
                                             // extract positions


        /**
         * @param idsGenes list of ids of a gene
         * @return List<Locus> : list of Locus corresponding to a list of idsGenes
         */
        public List<Locus> idListToLocus(List<String> idsGenes) {
                List<Locus> locusList = new ArrayList<>();
                String xmlGeneURL = XmlParse.getURLFromIds(idsGenes);
                org.w3c.dom.Document xmlDocument = XmlParse.document(xmlGeneURL);
                NodeList gNodeList = xmlDocument.getElementsByTagName("Entrezgene");

                for (int i = 0; i < gNodeList.getLength(); i++) {
                        Node currentGNode = gNodeList.item(i);
                        locusList.add(idToLocus(currentGNode)); // add the found locus for each id
                }
                return locusList;
        }


        /**
         * @param currentGNode general node corresponding to an idGene
         * @return Locus : Locus corresponding to an idGene (represented by a currentGNode)
         */
        private Locus idToLocus(Node currentGNode) {
                String chromosome;
                int start;
                int stop;

                try {
                        // value of the chromosome :
                        chromosome = XmlParse
                                        .getNodeFromPath(currentGNode, List.of("Entrezgene_source",
                                                        "BioSource", "BioSource_subtype",
                                                        "SubSource", "SubSource_name"))
                                        .getFirstChild().getNodeValue();
                        // Node containing the start and stop positions.
                        Node positionsNode = findPosition(currentGNode);
                        start = Integer.parseInt(
                                        XmlParse.getNodeFromPath(positionsNode, "Seq-interval_from")
                                                        .getFirstChild().getNodeValue())
                                        + OFFSET;
                        stop = Integer.parseInt(
                                        XmlParse.getNodeFromPath(positionsNode, "Seq-interval_to")
                                                        .getFirstChild().getNodeValue())
                                        + OFFSET;
                        return new Locus(chromosome, start, stop);
                } catch (NullPointerException e) {
                        System.out.println("Noeud" + e.getMessage()
                                        + "non trouvé dans l’arborescence");
                        e.printStackTrace();
                        return null;
                }
        }


        /**
         * @param currentGNode general node corresponding to an idGene
         * @return Node : contains the start and stop positions on 2 direct children
         */
        private Node findPosition(Node currentGNode) {
                // go down on the path :
                Node geneLocationHistoryNode = XmlParse.xmlCommentFinder(
                                XmlParse.getNodeFromPath(currentGNode, "Entrezgene_comments"),
                                "254", "Gene Location History", "Gene-commentary_comment");
                // find the node which has the highest release then date corresponding to the good
                // version
                Node versionNode = findVersionNode(geneLocationHistoryNode);

                // go down on the path :
                return XmlParse.getNodeFromPath(XmlParse.xmlCommentFinder(
                                XmlParse.xmlCommentFinder(versionNode, "25", "Primary Assembly",
                                                "Gene-commentary_comment"),
                                "1", "any", "Gene-commentary_seqs"),
                                List.of("Seq-loc", "Seq-loc_int", "Seq-interval"));
        }



        /**
         * @param geneLocationHistoryNode
         * @return Node : has the highest release then date corresponding to the good version
         */
        private Node findVersionNode(Node geneLocationHistoryNode) {
                NodeList annotationReleases = geneLocationHistoryNode.getChildNodes();
                ArrayList<XmlRelease> possibleNodesList = new ArrayList<>();
                for (int i = 0; i < annotationReleases.getLength(); i++) {
                        // add each node with it’s corresponding release and date
                        possibleNodesList.add(new XmlRelease(annotationReleases.item(i)));

                }
                // Keep only the highest date for each realease :
                XmlRelease.clean(possibleNodesList);

                // Classify by release descending order :
                XmlRelease.classify(possibleNodesList);

                // Select the first node with the good version :
                var nodeSelected = selectNode(possibleNodesList);
                if (nodeSelected.isPresent()) {
                        return nodeSelected.get();
                }
                return null; // return null if we didn’t find any node corresponding
        }


        /**
         * @param possibleNodesList
         * @return Optional<Node> : first Node corresponding to the version if it exists
         */
        private Optional<Node> selectNode(List<XmlRelease> possibleNodesList) {
                int i = 0;
                int length = possibleNodesList.size();
                String verString = this.version.toString();
                while (i < length) {
                        var thePossibleNode = XmlParse.getNodeFromPath(
                                        possibleNodesList.get(i).getNode(),
                                        List.of("Gene-commentary_comment", "Gene-commentary"));
                        if (Objects.equals(XmlParse
                                        .getNodeFromPath(thePossibleNode, "Gene-commentary_heading")
                                        .getFirstChild().getNodeValue().substring(0, 6),
                                        verString)) { // tests if the versions are the same
                                return Optional.of(XmlParse.getNodeFromPath(thePossibleNode,
                                                "Gene-commentary_comment"));
                        }
                        i += 1;
                }
                return Optional.empty(); // no node corresponding to the version found
        }
}
