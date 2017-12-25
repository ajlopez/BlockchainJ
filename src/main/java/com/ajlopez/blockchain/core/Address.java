package com.ajlopez.blockchain.core;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 31/08/2017.
 */
public class Address {
    private static Random random = new Random();

    private byte[] bytes;

    public Address() {
        this.bytes = new byte[20];
        random.nextBytes(this.bytes);
    }

    public Address(byte[] bytes) {
        if (bytes.length > 20)
            throw new IllegalStateException("Address too long");

        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof Address))
            return false;

        Address address = (Address)obj;

        return Arrays.equals(this.bytes, address.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }
}
