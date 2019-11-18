package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;

import java.util.Arrays;

/**
 * Created by ajlopez on 27/11/2018.
 */
public abstract class AbstractBytesValue {
    protected final byte[] bytes;

    public AbstractBytesValue(byte[] bytes, int length) {
        this(bytes, length, false);
    }

    public AbstractBytesValue(byte[] bytes, int length, boolean signed) {
        if (bytes == null)
            throw new IllegalArgumentException("Null byte array");

        if (bytes.length > length)
            throw new IllegalArgumentException("Too large byte array");

        if (bytes.length == length)
            this.bytes = bytes;
        else
            this.bytes = ByteUtils.copyBytes(bytes, length, false, signed);
    }

    // TODO consider immutability
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (this.getClass() != obj.getClass())
            return false;

        AbstractBytesValue value = (AbstractBytesValue)obj;

        return Arrays.equals(this.bytes, value.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    @Override
    public String toString() {
        return HexUtils.bytesToHexString(this.bytes, true);
    }
}
