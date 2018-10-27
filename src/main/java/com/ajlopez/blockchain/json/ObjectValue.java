package com.ajlopez.blockchain.json;

import java.util.Map;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class ObjectValue extends Value {
    private Map<String, Value> properties;

    public ObjectValue(Map<String, Value> properties) {
        super(ValueType.OBJECT, properties);
        this.properties = properties;
    }

    public Value getProperty(String name) {
        return this.properties.get(name);
    }
}
