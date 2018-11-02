package com.ajlopez.blockchain.json;

import java.util.Map;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class ObjectValue extends JsonValue {
    private Map<String, JsonValue> properties;

    public ObjectValue(Map<String, JsonValue> properties) {
        super(ValueType.OBJECT, properties);
        this.properties = properties;
    }

    public JsonValue getProperty(String name) {
        return this.properties.get(name);
    }

    public JsonValue getProperty(String name, String ...names) {
        JsonValue value = this.getProperty(name);

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

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append('{');

        int nproperty = 0;

        for (Map.Entry<String, JsonValue> entry: this.properties.entrySet()) {
            if (nproperty > 0)
                buffer.append(',');

            buffer.append(' ');

            buffer.append((new StringValue(entry.getKey())).toString());
            buffer.append(": ");
            buffer.append(entry.getValue().toString());

            nproperty++;
        }

        if (nproperty > 0)
            buffer.append(' ');

        buffer.append('}');

        return buffer.toString();
    }
}
