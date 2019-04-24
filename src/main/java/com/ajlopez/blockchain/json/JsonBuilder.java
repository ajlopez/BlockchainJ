package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 02/11/2018.
 */
public class JsonBuilder {
    private JsonValue value;

    public JsonBuilder value(Object obj) {
        if (obj == null) {
            this.value = new JsonNullValue();
            return this;
        }
        else
            return this.value(obj.toString());
    }

    public JsonBuilder value(String value) {
        if (value == null)
            this.value = new JsonNullValue();
        else
            this.value = new JsonStringValue(value);

        return this;
    }

    public JsonBuilder value(int value) {
        this.value = new JsonNumericValue(Integer.toString(value));

        return this;
    }

    public JsonBuilder value(boolean value) {
        this.value = new JsonBooleanValue(value);

        return this;
    }

    public JsonBuilder value(JsonValue value) {
        this.value = value;

        return this;
    }

    public JsonBuilder name(String name) {
        throw new UnsupportedOperationException();
    }

    public JsonBuilder array() {
        return new JsonArrayBuilder(this);
    }

    public JsonBuilder object() {
        return new JsonObjectBuilder(this);
    }

    public JsonBuilder end() {
        return this;
    }

    public JsonValue build() {
        return this.value;
    }
}
