package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Angel on 4/23/2019.
 */
public class JsonNullValueTest {
    @Test
    public void createJsonNullValue() {
        JsonValue jsonValue = JsonNullValue.getInstance();

        Assert.assertEquals(JsonValueType.NULL, jsonValue.getType());
        Assert.assertNull(jsonValue.getValue());
        Assert.assertEquals("null", jsonValue.toString());
    }
}
