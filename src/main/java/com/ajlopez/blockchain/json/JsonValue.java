package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 27/10/2018.
 */
public abstract class JsonValue {
    private JsonValueType type;
    private Object value;

    public JsonValue(JsonValueType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public JsonValueType getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }
}
