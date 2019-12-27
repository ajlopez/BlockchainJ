package com.ajlopez.blockchain.utils;

import java.util.Arrays;

/**
 * Created by ajlopez on 21/11/2017.
 */
public class ByteArrayWrapper {
    private final byte[] bytes;
    private final int hashCode;

    public ByteArrayWrapper(byte[] bytes) {
        this.bytes = bytes;
        this.hashCode = Arrays.hashCode(bytes);
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ByteArrayWrapper))
            return false;

        return Arrays.equals(this.bytes, ((ByteArrayWrapper)obj).bytes);
    }
}
