package com.ajlopez.blockchain.json;

/**
 * Created by Angel on 23/04/2019.
 */
public class JsonNullValue extends JsonValue {
    public JsonNullValue() {
        super(JsonValueType.NULL, null);
    }

    @Override
    public String toString() {
        return "null";
    }
}
