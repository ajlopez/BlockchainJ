package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.math.BigInteger;

/**
 * Created by ajlopez on 24/02/2019.
 */
public class RLPEncoder {
    private RLPEncoder() {}

    public static byte[] encodeAddress(Address address) {
        return RLP.encode(address.getBytes());
    }

    public static Address decodeAddress(byte[] data) {
        return new Address(RLP.decode(data));
    }

    public static byte[] encodeCoin(BigInteger value) {
        return RLP.encode(ByteUtils.normalizedBytes(value.toByteArray()));
    }

    public static BigInteger decodeCoin(byte[] data) {
        return new BigInteger(1, RLP.decode(data));
    }

    public static byte[] encodeUnsignedLong(long value) {
        return RLP.encode(ByteUtils.unsignedLongToNormalizedBytes(value));
    }

    public static long decodeUnsignedLong(byte[] data) {
        return ByteUtils.bytesToUnsignedLong(RLP.decode(data));
    }
}
