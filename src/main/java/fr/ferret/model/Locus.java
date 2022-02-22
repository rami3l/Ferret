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

    /**
     * Creates a List of {@link Locus} from a List of ids <br/>
     * For example {@code Locus.from(List.of("1234", "1235"))}
     *
     * @param ids The List of ids we want to convert to Locus
     * @return The List of {@link Locus}
     */
    public static List<Locus> from(List<String> ids) {
        // list des ids séparés par des virgules
        var idParam = String.join(",", ids);

        // url créée à partir de urlFormat et de la liste des ids
        var uri = String.format(urlFormat, idParam);

        // Creation de la requête et du client http
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(uri)).GET().build();

        try {
            // On envoie la requête
            var response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

            // On découpe la réponse par id
            // (les infos de chaque id de la requête sont séparées par un retour à la ligne)
            var chromosomeInfos = Stream.of(response.split("\n\n"));

            // On retourne la liste des Locus créés à partir de la réponse
            return chromosomeInfos.map(Locus::getFrom).toList();

        } catch (Exception e) {
            throw new RuntimeException("Impossible to get locus", e);
        }
    }

    private static Locus getFrom(String chromosomeInfo) {
        // On sépare les lignes et on crée une map (titre ligne: contenu)
        var lines = Stream.of(chromosomeInfo.split("\n"));
        var entries = lines.map(line -> line.split(":"))
            .filter(elems -> elems.length > 1)
            .collect(Collectors.toMap(elems -> elems[0], elems -> elems[1]));

        // On récupère le contenu des lignes dont le titre est "Chromosome" et "Annotation"
        // Et on découpe ce contenu de manière à obtenir la valeur voulue
        var chr = entries.get("Chromosome").split(";")[0].trim();
        var startEnd = entries.get("Annotation").split("[(]")[1].split("\\.\\.");
        var start = startEnd[0];
        var end = startEnd[1].split("[)]")[0];

        // On retourne un Locus créé à partir des valeurs
        return new Locus(chr, Integer.parseInt(start), Integer.parseInt(end));
    }

    @Override public String toString() {
        return "Locus{" + "chromosome='" + chromosome + '\'' + ", start=" + start + ", stop=" + stop
            + '}';
    }
}
