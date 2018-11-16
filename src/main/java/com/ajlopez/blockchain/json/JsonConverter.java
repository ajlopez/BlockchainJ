package com.ajlopez.blockchain.json;

import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;

import java.math.BigInteger;

/**
 * Created by ajlopez on 14/11/2018.
 */
public class JsonConverter {
    private JsonConverter() {

    }

    public static JsonValue convert(String value) {
        return new JsonStringValue(value);
    }

    public static JsonValue convert(int value) {
        return new JsonNumericValue(value);
    }

    public static JsonValue convert(BigInteger value) {
        byte[] bytes = value.toByteArray();
        bytes = ByteUtils.copyBytes(bytes, 32);
        return new JsonStringValue(HexUtils.bytesToHexString(bytes));
    }

    public static JsonValue convert(Object value) {
        return new JsonStringValue(value.toString());
    }
}
