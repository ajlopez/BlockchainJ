package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 23/04/2019.
 */
public class JsonNullValue extends JsonValue {
    private static final JsonNullValue instance = new JsonNullValue();

    private JsonNullValue() {
        super(JsonValueType.NULL, null);
    }

    public static JsonNullValue getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "null";
    }
}
