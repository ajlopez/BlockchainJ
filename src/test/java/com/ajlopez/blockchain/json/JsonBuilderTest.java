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
}
