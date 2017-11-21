package com.ajlopez.blockchain.utils;

import java.util.Arrays;

/**
 * Created by ajlopez on 21/11/2017.
 */
public class ByteArrayWrapper {
    private byte[] bytes;

    public ByteArrayWrapper(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ByteArrayWrapper))
            return false;

        return Arrays.equals(this.bytes, ((ByteArrayWrapper)obj).bytes);
    }
}
