package com.ajlopez.blockchain.core.types;

import java.util.Random;

/**
 * Created by ajlopez on 31/08/2017.
 */
public class Address extends AbstractBytesValue {
    public static final int ADDRESS_BYTES = 20;

    public Address(byte[] bytes) {
        super(bytes, ADDRESS_BYTES);
    }

    @Override
    public int hashOffset() {
        return 23;
    }
}
