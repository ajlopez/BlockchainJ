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
}
