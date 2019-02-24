package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Address;

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
}
