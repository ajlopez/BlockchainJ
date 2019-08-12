package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtils;

/**
 * Created by ajlopez on 12/08/2019.
 */
public class Chunk {
    private final Hash hash;
    private byte[] data;

    public Chunk(byte[] data) {
        this.data = data;
        this.hash = HashUtils.calculateHash(data);
    }

    public Hash getHash() { return this.hash; }

    public byte[] getData() { return this.data; }
}
