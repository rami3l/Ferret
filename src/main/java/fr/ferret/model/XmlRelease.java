package fr.ferret.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.w3c.dom.Node;
import lombok.var;

public class XmlRelease {
    private Node node;
    private int release;
    private int date;

    public XmlRelease(Node node) {
        this.node = node;
        String releaseString = XmlParse.getChildByName(node, "Gene-commentary_heading")
                .getFirstChild().getNodeValue();
        String[] releaseWords = releaseString.split(" ");
        releaseWords = releaseWords[releaseWords.length - 1].split("\\.");
        if (releaseWords.length == 2) {
            release = Integer.parseInt(releaseWords[0]);
            this.date = Integer.parseInt(releaseWords[1]);
        } else {
            this.release = Integer.parseInt(releaseWords[0]);
            this.date = 0; // date la plus petite possible
        }
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    static void elementExchange(ArrayList<XmlRelease> t, int m, int n) {
        var temp = t.get(m);

        t.set(m, t.get(n));
        t.set(n, temp);
    }


    static int partition(ArrayList<XmlRelease> t, int m, int n) {
        var v = t.get(m); // valeur pivot
        int i = m - 1;
        int j = n + 1; // indice final du pivot

        while (true) {
            do {
                j--;
            } while (t.get(j).releaseInf(v));
            do {
                i++;
            } while (v.releaseInf(t.get(j)));
            if (i < j) {
                elementExchange(t, i, j);
            } else {
                return j;
            }
        }
    }

    static void classify(ArrayList<XmlRelease> t, int m, int n) {
        if (m < n) {
            int p = partition(t, m, n);
            classify(t, m, p);
            classify(t, p + 1, n);
        }
    }


    static void classify(ArrayList<XmlRelease> t) {
        classify(t, 0, t.size() - 1);
    }

    public boolean releaseInf(Object o) {
        return this.release < ((XmlRelease) o).release;
    }

    public static void clean(List<XmlRelease> possibleNodesList) {
        int i = 0;
        while (i < possibleNodesList.size()) {
            int j = 0;
            while (j < possibleNodesList.size()) {
                XmlRelease xmlReleaseI = possibleNodesList.get(i);
                XmlRelease xmlReleaseJ = possibleNodesList.get(j);
                if (xmlReleaseI.release == xmlReleaseJ.release
                        && xmlReleaseI.date != xmlReleaseJ.date) {
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
