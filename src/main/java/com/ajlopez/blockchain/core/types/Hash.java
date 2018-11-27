package com.ajlopez.blockchain.core.types;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Hash extends AbstractBytesValue {
    public static final int HASH_BYTES = 32;
    public static Hash emptyHash = new Hash(new byte[HASH_BYTES]);

    public Hash(byte[] bytes) {
        super(bytes, HASH_BYTES);
    }

    @Override
    public int hashOffset() {
        return 17;
    }
}
