package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;

import java.util.Arrays;

/**
 * Created by ajlopez on 27/11/2018.
 */
public abstract class AbstractBytesValue {
    private byte[] bytes;

    public AbstractBytesValue(byte[] bytes, int length) {
        if (bytes == null)
            throw new IllegalArgumentException("Null byte array");

        if (bytes.length > length)
            throw new IllegalArgumentException("Too large byte array");

        this.bytes = ByteUtils.copyBytes(bytes, length, false);
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof AbstractBytesValue))
            return false;

        AbstractBytesValue value = (AbstractBytesValue)obj;

        if (this.hashOffset() != value.hashOffset())
            return false;

        return Arrays.equals(this.bytes, value.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes) + this.hashOffset();
    }

    @Override
    public String toString() {
        return HexUtils.bytesToHexString(this.bytes, true);
    }

    protected abstract int hashOffset();
}
