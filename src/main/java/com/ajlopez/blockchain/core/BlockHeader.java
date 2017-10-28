package com.ajlopez.blockchain.core;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockHeader {
    private long number;
    private Hash parentHash;
    private Hash hash;

    public BlockHeader(long number, Hash parentHash) {
        this.number = number;
        this.parentHash = parentHash;
        this.hash = new Hash();
    }

    public long getNumber() {
        return this.number;
    }

    public Hash getHash() {
        return this.hash;
    }

    public Hash getParentHash() {
        return this.parentHash;
    }
}
