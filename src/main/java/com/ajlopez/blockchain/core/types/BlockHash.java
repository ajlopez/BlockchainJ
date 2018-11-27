package com.ajlopez.blockchain.core.types;

/**
 * Created by ajlopez on 22/03/2018.
 */
public class BlockHash extends Hash {
    public BlockHash(byte[] bytes) {
        super(bytes);
    }

    public BlockHash(Hash hash) {
        this(hash.getBytes());
    }

    @Override
    public int hashOffset() {
        return 31;
    }
}
