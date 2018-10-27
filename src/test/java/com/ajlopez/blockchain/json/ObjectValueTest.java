package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class ObjectValueTest {
    @Test
    public void createObjectValueWithTwoProperties() {
        Value name = new StringValue("adam");
        Value age = new NumericValue("900");
        Map<String, Value> properties = new HashMap<>();
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
}

