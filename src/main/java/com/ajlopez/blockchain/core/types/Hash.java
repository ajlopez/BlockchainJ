package com.ajlopez.blockchain.core.types;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Hash {
    private static Hash emptyHash = new Hash(new byte[32]);
    private static Random random = new Random();

    private byte[] bytes;

    public Hash() {
        this.bytes = new byte[32];
        random.nextBytes(this.bytes);
    }

    public Hash(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Hash emptyHash() { return emptyHash; }

    public byte[] getBytes() {
        return this.bytes;
    }

    public boolean isEmpty() {
        return emptyHash.equals(this);
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
