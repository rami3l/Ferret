package fr.ferret.model;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;

/**
 * Allow to have the release and the date of a specified node
 */
public class XmlRelease {
    private Node node;
    private int release;
    private int date;

    public XmlRelease(Node node) {
        this.node = node;
        String releaseString = XmlParse.getNodeFromPath(node, "Gene-commentary_heading")
                .getFirstChild().getNodeValue();
        String[] releaseWords = releaseString.split(" ");
        releaseWords = releaseWords[releaseWords.length - 1].split("\\.");
        if (releaseWords.length == 2) {
            release = Integer.parseInt(releaseWords[0]);
            this.date = Integer.parseInt(releaseWords[1]);
        } else {
            this.release = Integer.parseInt(releaseWords[0]);
            this.date = 0; // lowest date possible
        }
    }


    /**
     * @return Node
     */
    public Node getNode() {
        return node;
    }


    /**
     * @param node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * @param list
     * @param m
     * @param n
     * 
     *        exchange list[m] and list[n]
     */
    static void elementExchange(ArrayList<XmlRelease> list, int m, int n) {
        var temp = list.get(m);

        list.set(m, list.get(n));
        list.set(n, temp);
    }

    /**
     * @param list
     * @param m
     * @param n
     * @return int
     */
    static int partition(ArrayList<XmlRelease> list, int m, int n) {
        var v = list.get(m); // pivot value
        int i = m - 1;
        int j = n + 1; // pivot final index

        while (true) {
            do {
                j--;
            } while (list.get(j).releaseInf(v));
            do {
                i++;
            } while (v.releaseInf(list.get(j)));
            if (i < j) {
                elementExchange(list, i, j);
            } else {
                return j;
            }
        }
    }


    /**
     * @param list
     * @param m
     * @param n
     */
    static void classify(ArrayList<XmlRelease> list, int m, int n) {
        if (m < n) {
            int p = partition(list, m, n);
            classify(list, m, p);
            classify(list, p + 1, n);
        }
    }



    /**
     * @param list : to classify by descending order
     */
    static void classify(ArrayList<XmlRelease> list) {
        classify(list, 0, list.size() - 1);
    }


    /**
     * @param o
     * @return boolean
     */
    public boolean releaseInf(Object o) {
        return this.release < ((XmlRelease) o).release;
    }


    /**
     * @param possibleNodesList
     * 
     *        Select the node which has the highest date for each release
     */
    public static void clean(List<XmlRelease> possibleNodesList) {
        int i = 0;
        while (i < possibleNodesList.size()) {
            int j = 0;
            while (j < possibleNodesList.size()) {
                XmlRelease xmlReleaseI = possibleNodesList.get(i);
                XmlRelease xmlReleaseJ = possibleNodesList.get(j);
                if (xmlReleaseI.release == xmlReleaseJ.release
                        && xmlReleaseI.date != xmlReleaseJ.date) {
                    // If we remove a node, we will compare to different nodes next time with the
                    // same (i, j)
                    if (xmlReleaseI.date < xmlReleaseJ.date) {
                        possibleNodesList.remove(xmlReleaseI);
                    } else {
                        possibleNodesList.remove(xmlReleaseJ);
                    }
                } else {
                    j += 1;
                }
            }
            i += 1;
        }
    }


}
