package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.utils.ByteUtils;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Hash extends AbstractBytesValue {
    public static final int HASH_BYTES = 32;

    public Hash(byte[] bytes) {
        super(bytes, HASH_BYTES);
    }

    public long asLong() {
        return ByteUtils.bytesToLong(this.bytes, HASH_BYTES - Long.BYTES);
    }

    public int asInteger() {
        return ByteUtils.bytesToInteger(this.bytes, HASH_BYTES - Integer.BYTES);
    }
}
