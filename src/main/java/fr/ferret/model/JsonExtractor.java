package fr.ferret.model;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.InputStream;

public class JsonExtractor {

    private final DocumentContext document;

    public JsonExtractor(InputStream json) {
        document = JsonPath.parse(json);
    }

    public <T> T get(String path) {
        return document.read(path);
    }

}
