package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.utils.ByteUtils;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 31/08/2017.
 */
public class Address {
    public static final int ADDRESS_LENGTH = 20;

    private static Random random = new Random();

    private byte[] bytes;

    public Address() {
        this.bytes = new byte[20];
        random.nextBytes(this.bytes);
    }

    public Address(byte[] bytes) {
        if (bytes.length > ADDRESS_LENGTH)
            throw new IllegalArgumentException("Address too long");

        this.bytes = ByteUtils.copyBytes(bytes, ADDRESS_LENGTH);
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
