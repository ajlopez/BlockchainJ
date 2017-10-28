package com.ajlopez.blockchain.core;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Block {
    private BlockHeader header;

    public Block(long number, Hash parentHash) {
        this.header = new BlockHeader(number, parentHash);
    }

    public Block(BlockHeader header) {
        this.header = header;
    }

    public BlockHeader getHeader() {
        return this.header;
    }

    public long getNumber() {
        return this.header.getNumber();
    }

    public Hash getHash() {
        return this.header.getHash();
    }

    public Hash getParentHash() {
        return this.header.getParentHash();
    }
}
