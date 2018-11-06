package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class JsonBooleanValueTest {
    @Test
    public void createFalseBooleanValue() {
        JsonBooleanValue value = new JsonBooleanValue(false);

        Assert.assertEquals(ValueType.BOOLEAN, value.getType());
        Assert.assertEquals(false, value.getValue());
    }

    @Test
    public void createTrueBooleanValue() {
        JsonBooleanValue value = new JsonBooleanValue(true);

        Assert.assertEquals(ValueType.BOOLEAN, value.getType());
        Assert.assertEquals(true, value.getValue());
    }

    @Test
    public void booleanValuesToString() {
        JsonBooleanValue trueValue = new JsonBooleanValue(true);
        JsonBooleanValue falseValue = new JsonBooleanValue(false);

        Assert.assertEquals("true", trueValue.toString());
        Assert.assertEquals("false", falseValue.toString());
    }
}

