package com.ajlopez.blockchain.json;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.utils.HexUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by ajlopez on 14/11/2018.
 */
public class JsonConverterTest {
    private static Random random = new Random();

    @Test
    public void convertString() {
        JsonValue result = JsonConverter.convert("foo");

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.STRING, result.getType());
        Assert.assertEquals("foo", result.getValue());
    }

    @Test
    public void convertInteger() {
        JsonValue result = JsonConverter.convert(42);

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.NUMBER, result.getType());
        Assert.assertEquals("42", result.getValue());
    }

    @Test
    public void convertAddress() {
        byte[] bytes = new byte[Address.ADDRESS_LENGTH];
        random.nextBytes(bytes);
        Address address = new Address(bytes);

        JsonValue result = JsonConverter.convert(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.STRING, result.getType());
        Assert.assertEquals(HexUtils.bytesToHexString(bytes, true), result.getValue());
    }
}
