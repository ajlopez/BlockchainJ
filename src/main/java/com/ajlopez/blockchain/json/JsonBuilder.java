package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 02/11/2018.
 */
public class JsonBuilder {
    private Object value;
    private ValueType type;

    public JsonBuilder value(String value) {
        this.value = value;
        this.type = ValueType.STRING;

        return this;
    }

    public JsonBuilder value(int value) {
        this.value = Integer.toString(value);
        this.type = ValueType.NUMBER;

        return this;
    }

    public JsonValue build() {
        if (this.type == ValueType.NUMBER)
            return new NumericValue((String)this.value);

        return new StringValue((String)this.value);
    }
}
