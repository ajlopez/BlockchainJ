package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class JsonStringValueTest {
    @Test
    public void createStringValue() {
        JsonStringValue value = new JsonStringValue("foo");

        Assert.assertEquals(JsonValueType.STRING, value.getType());
        Assert.assertEquals("foo", value.getValue());
    }

    @Test
    public void simpleStringValueToString() {
        JsonStringValue value = new JsonStringValue("foo");

        Assert.assertEquals("\"foo\"", value.toString());
    }

    @Test
    public void simpleStringValueToStringWithEscapedCharacters() {
        JsonStringValue value = new JsonStringValue("\n\r\tfoo\\\"");

        Assert.assertEquals("\"\\n\\r\\tfoo\\\\\\\"\"", value.toString());
    }
}

