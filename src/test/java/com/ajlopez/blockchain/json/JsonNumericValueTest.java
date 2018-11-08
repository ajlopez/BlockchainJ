package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class JsonNumericValueTest {
    @Test
    public void createNumericValue() {
        JsonNumericValue value = new JsonNumericValue("42");

        Assert.assertEquals(JsonValueType.NUMBER, value.getType());
        Assert.assertEquals("42", value.getValue());
    }

    @Test
    public void simpleNumericValueToString() {
        JsonNumericValue value = new JsonNumericValue("42");

        Assert.assertEquals("42", value.toString());
    }
}

