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

    public Value getProperty(String name, String ...names) {
        Value value = this.getProperty(name);

        for (int k = 0; k < names.length; k++)
            value = ((ObjectValue)value).getProperty(names[k]);

        return value;
    }

    public boolean hasProperty(String name) {
        return this.properties.containsKey(name);
    }

    public int noProperties() {
        return this.properties.size();
    }
}
