package com.ajlopez.blockchain.core.types;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Hash {
    private byte[] bytes;

    public Hash(byte[] bytes) {
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