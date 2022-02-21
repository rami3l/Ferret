package fr.ferret.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Locus(String chromosome, int start, int stop) {
    private static String urlFormat = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=gene&id=%s&retmode=text";

    public static List<Locus> from(List<String> ids) {
        var client = HttpClient.newHttpClient();
        var idParam = String.join(",", ids);
        var uri = String.format(urlFormat, idParam);
        var request = HttpRequest.newBuilder(URI.create(uri)).GET().build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            var chromosomeInfos = Stream.of(response.split("\n\n"));
            return chromosomeInfos.map(Locus::getFrom).toList();
        } catch (Exception e) {
            throw new RuntimeException("Impossible to get locus", e);
        }
    }

    private static Locus getFrom(String chromosomeInfo) {
        var lines = Stream.of(chromosomeInfo.split("\n"));
        var entries = lines.map(line -> line.split(":"))
            .filter(elems -> elems.length > 1)
            .collect(Collectors.toMap(elems -> elems[0], elems -> elems[1]));
        var chr = entries.get("Chromosome").split(";")[0];
        var startEnd = entries.get("Annotation").split("[(]")[1].split("\\.\\.");
        var start = startEnd[0];
        var end = startEnd[1].split("[)]")[0];
        return new Locus(chr, Integer.parseInt(start), Integer.parseInt(end));
    }

    @Override public String toString() {
        return "Locus{" + "chromosome='" + chromosome + '\'' + ", start=" + start + ", stop=" + stop
            + '}';
    }
}
