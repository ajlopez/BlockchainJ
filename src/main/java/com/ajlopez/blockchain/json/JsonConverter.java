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

    public static JsonValue convert(int value) {
        return new JsonNumericValue(value);
    }

    public static JsonValue convert(Object value) {
        return new JsonStringValue(value.toString());
    }
}
