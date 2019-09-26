package com.ajlopez.blockchain.json;

import java.io.IOException;
import java.io.StringWriter;

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

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        try {
            jsonWriter.write(this);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }
}
