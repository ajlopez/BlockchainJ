package com.ajlopez.blockchain.core.types;

/**
 * Created by ajlopez on 31/08/2017.
 */
public class Address extends AbstractBytesValue {
    public static final int ADDRESS_BYTES = 20;

    public static final Address ZERO = new Address(new byte[0]);

    public Address(byte[] bytes) {
        super(bytes, ADDRESS_BYTES);
    }

    @Override
    public int hashOffset() {
        return 23;
    }
}
