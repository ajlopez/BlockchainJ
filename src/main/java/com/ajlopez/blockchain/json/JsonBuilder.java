package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 02/11/2018.
 */
public class JsonBuilder {
    private Object value;

    public JsonBuilder value(String value) {
        this.value = value;

        return this;
    }

    public JsonValue build() {
        return new StringValue((String)this.value);
    }
}
