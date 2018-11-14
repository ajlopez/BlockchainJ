package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 14/11/2018.
 */
public class JsonConverterTest {
    @Test
    public void convertString() {
        JsonValue result = JsonConverter.convert("foo");

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.STRING, result.getType());
        Assert.assertEquals("foo", result.getValue());
    }
}
