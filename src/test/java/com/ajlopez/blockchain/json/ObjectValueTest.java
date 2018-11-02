package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class ObjectValueTest {
    @Test
    public void createObjectValueWithTwoProperties() {
        JsonValue name = new StringValue("adam");
        JsonValue age = new NumericValue("900");
        Map<String, JsonValue> properties = new LinkedHashMap<>();
        properties.put("name", name);
        properties.put("age", age);

        ObjectValue value = new ObjectValue(properties);

        Assert.assertTrue(value.hasProperty("name"));
        Assert.assertTrue(value.hasProperty("age"));
        Assert.assertFalse(value.hasProperty("weight"));

        Assert.assertEquals(ValueType.OBJECT, value.getType());
        Assert.assertSame(properties, value.getValue());
        Assert.assertSame(name, value.getProperty("name"));
        Assert.assertSame(age, value.getProperty("age"));
    }

    @Test
    public void objectValueWithTwoPropertiesToString() {
        JsonValue name = new StringValue("adam");
        JsonValue age = new NumericValue("900");
        Map<String, JsonValue> properties = new LinkedHashMap<>();
        properties.put("name", name);
        properties.put("age", age);

        ObjectValue value = new ObjectValue(properties);

        Assert.assertEquals("{ \"name\": \"adam\", \"age\": 900 }", value.toString());
    }

    @Test
    public void objectValueWithNoPropertiesToString() {
        Map<String, JsonValue> properties = new LinkedHashMap<>();

        ObjectValue value = new ObjectValue(properties);

        Assert.assertEquals("{}", value.toString());
    }
}

