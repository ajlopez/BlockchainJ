package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Address;

/**
 * Created by ajlopez on 27/09/2017.
 */
public class AddressEncoder {
    public static byte[] encode(Address address) {
        byte[] rlpBytes = RLP.encode(address.getBytes());

        return RLP.encodeList(rlpBytes);
    }

    public static Address decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        return new Address(RLP.decode(bytes[0]));
    }
}

