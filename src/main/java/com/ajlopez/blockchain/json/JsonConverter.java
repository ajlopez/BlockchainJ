package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 14/11/2018.
 */
public class JsonConverter {
    private JsonConverter() {

    }

    public static JsonValue convert(String value) {
        return new JsonStringValue(value);
    }
}
