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
        String releaseString =
                XmlParse.getChildByName(node, "Gene-commentary_heading").getNodeValue();
        String[] releaseWords = releaseString.split(" .");
        if (isNumeric(releaseWords[-2])) {
            release = Integer.parseInt(releaseWords[-2]);
            this.date = Integer.parseInt(releaseWords[-1]);
        } else {
            this.release = Integer.parseInt(releaseWords[-1]);
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
            } while (v.releaseInf(t.get(j)));
            do {
                i++;
            } while (t.get(i).releaseInf(v));
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
        for (XmlRelease xmlRelease : possibleNodesList) {
            for (XmlRelease xmlRelease2 : possibleNodesList) {
                if (xmlRelease != xmlRelease2 && xmlRelease.release == xmlRelease2.release) {
                    if (xmlRelease.date > xmlRelease2.date) {
                        possibleNodesList.remove(xmlRelease2);
                    } else if (xmlRelease.date < xmlRelease2.date) {
                        possibleNodesList.remove(xmlRelease);
                    }
                }
            }
        }
    }


}
