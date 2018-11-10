package com.ajlopez.blockchain.json;

import java.util.List;

/**
 * Created by ajlopez on 29/10/2018.
 */
public class JsonArrayValue extends JsonValue {
    private List<JsonValue> values;

    public JsonArrayValue(List<JsonValue> values) {
        super(JsonValueType.ARRAY, values);
        this.values = values;
    }

    public int size() {
        return this.values.size();
    }

    public JsonValue getValue(int index) {
        return this.values.get(index);
    }

    public List<JsonValue> getValues() { return this.values; }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append('[');

        int nvalue = 0;

        for (JsonValue value : this.values) {
            if (nvalue > 0)
                buffer.append(',');

            buffer.append(' ');

            buffer.append(value.toString());

            nvalue++;
        }

        if (nvalue > 0)
            buffer.append(' ');

        buffer.append(']');

        return buffer.toString();
    }}
