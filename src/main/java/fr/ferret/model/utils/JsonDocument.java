package fr.ferret.model.utils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.InputStream;

/**
 * Object encapsulating a json document with its parser, so that you can extract multiple
 * information from a json {@link InputStream} without parsing (and downloading it) each time
 */
public class JsonDocument {

    private final DocumentContext document;

    /**
     * Creates a {@link JsonDocument} encapsulating the given json
     *
     * @param json The json {@link InputStream} to extract information from
     */
    public JsonDocument(InputStream json) {
        document = JsonPath.parse(json);
    }

    /**
     * Reads the given json path from the document
     *
     * @param path The json path to read
     * @return result
     */
    public <T> T get(String path) {
        return document.read(path);
    }

}
