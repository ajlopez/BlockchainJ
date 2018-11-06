package com.ajlopez.blockchain.json;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ajlopez on 04/11/2018.
 */
public class JsonObjectBuilder extends JsonBuilder {
    private JsonBuilder parent;
    private String name;
    Map<String, JsonValue> properties = new LinkedHashMap<>();

    public JsonObjectBuilder(JsonBuilder parent) {
        this.parent = parent;
    }

    @Override
    public JsonBuilder value(int value) {
        super.value(value);
        properties.put(this.name, super.build());

        return this;
    }

    @Override
    public JsonBuilder value(boolean value) {
        super.value(value);
        properties.put(this.name, super.build());

        return this;
    }

    @Override
    public JsonBuilder value(String value) {
        super.value(value);
        properties.put(this.name, super.build());

        return this;
    }

    @Override
    public JsonBuilder name(String name) {
        this.name = name;

        return this;
    }

    @Override
    public JsonValue build() {
        return new JsonObjectValue(this.properties);
    }

    @Override
    public JsonBuilder end() {
        parent.value(this.build());

        return parent;
    }
}
