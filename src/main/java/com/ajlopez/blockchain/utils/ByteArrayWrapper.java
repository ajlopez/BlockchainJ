package com.ajlopez.blockchain.utils;

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
}
