package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class StringValueTest {
    @Test
    public void createStringValue() {
        StringValue value = new StringValue("foo");

        Assert.assertEquals(ValueType.STRING, value.getType());
        Assert.assertEquals("foo", value.getValue());
    }

    @Test
    public void simpleStringValueToString() {
        StringValue value = new StringValue("foo");

        Assert.assertEquals("\"foo\"", value.toString());
    }
}

