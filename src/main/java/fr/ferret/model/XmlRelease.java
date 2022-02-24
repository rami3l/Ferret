package fr.ferret.model;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import lombok.Getter;
import lombok.Setter;

/**
 * Allow to have the release and the date of a specified node
 */
public class XmlRelease {
    @Getter
    @Setter
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
     * exchange list[m] and list[n]
     * 
     * @param list
     * @param m
     * @param n
     */
    private static void elementExchange(List<XmlRelease> list, int m, int n) {
        var temp = list.get(m);

        list.set(m, list.get(n));
        list.set(n, temp);
    }

    /**
     * source : internet
     * 
     * @param list
     * @param m
     * @param n
     * @return int
     */
    private static int partition(List<XmlRelease> list, int m, int n) {
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
    private static void classify(List<XmlRelease> list, int m, int n) {
        if (m < n) {
            int p = partition(list, m, n);
            classify(list, m, p);
            classify(list, p + 1, n);
        }
    }



    /**
     * @param list : to classify by descending order
     */
    public static void classify(List<XmlRelease> list) {
        classify(list, 0, list.size() - 1);
    }


    /**
     * @param xmlRelease The release to compare this release to
     * @return boolean : True if the actual release is inferior to xmlRelease
     */
    public boolean releaseInf(XmlRelease xmlRelease) {
        return this.release < xmlRelease.release;
    }


    /**
     * Select the node which has the highest date for each release
     *
     * @param possibleNodesList
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
