package com.ajlopez.blockchain.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by ajlopez on 06/09/2019.
 */
public class JsonWriter {
    private final Writer writer;

    public JsonWriter(Writer writer) {
        this.writer = writer;
    }

    public void write(JsonValue jsonValue) throws IOException {
        // TODO avoid full toString, write each value type
        this.writer.write(jsonValue.toString());
    }
}
