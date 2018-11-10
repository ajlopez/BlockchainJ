package com.ajlopez.blockchain.core.types;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Hash {
    public static final int BYTES = 32;
    public static Hash emptyHash = new Hash(new byte[32]);

    private byte[] bytes;

    public Hash(byte[] bytes) {
        if (bytes == null)
            throw new IllegalArgumentException("Null byte array");

        if (bytes.length > 32)
            throw new IllegalArgumentException("Too large byte array");

        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof Hash))
            return false;

        Hash hash = (Hash)obj;

        return Arrays.equals(this.bytes, hash.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }
}
