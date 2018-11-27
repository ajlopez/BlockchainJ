package com.ajlopez.blockchain.core.types;

import java.util.Random;

/**
 * Created by ajlopez on 31/08/2017.
 */
public class Address extends AbstractBytesValue {
    public static final int ADDRESS_BYTES = 20;

    private static Random random = new Random();

    public Address() {
        super(randomBytes(), ADDRESS_BYTES);
    }

    public Address(byte[] bytes) {
        super(bytes, ADDRESS_BYTES);
    }

    @Override
    public int hashOffset() {
        return 23;
    }

    private static byte[] randomBytes() {
        byte[] bytes = new byte[ADDRESS_BYTES];
        random.nextBytes(bytes);

        return bytes;
    }
}
