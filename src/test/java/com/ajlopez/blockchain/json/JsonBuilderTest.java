package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 02/11/2018.
 */
public class JsonBuilderTest {
    @Test
    public void buildStringValue() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.value("foo").build();

        Assert.assertNotNull(result);
        Assert.assertEquals(ValueType.STRING, result.getType());
        Assert.assertEquals("foo", result.getValue());
    }

    @Test
    public void buildNumericValueFromInteger() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.value(42).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(ValueType.NUMBER, result.getType());
        Assert.assertEquals("42", result.getValue());
    }

    @Test
    public void buildBooleanValue() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.value(true).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(ValueType.BOOLEAN, result.getType());
        Assert.assertEquals(true, result.getValue());
    }
}
