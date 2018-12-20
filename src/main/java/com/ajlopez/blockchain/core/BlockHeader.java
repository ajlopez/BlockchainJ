package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.BlockHeaderEncoder;
import com.ajlopez.blockchain.utils.HashUtils;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockHeader {
    private final long number;
    private final BlockHash parentHash;
    private final Hash transactionsHash;
    private final Hash stateRootHash;
    private final long timestamp;
    private final Address coinbase;

    private BlockHash hash;

    public BlockHeader(long number, BlockHash parentHash, Hash transactionsHash, Hash stateRootHash, long timestamp, Address coinbase) {
        if (number < 0)
            throw new IllegalStateException("Negative number in block header");

        this.number = number;
        this.parentHash = parentHash == null ? new BlockHash(Hash.EMPTY_HASH) : parentHash;
        this.transactionsHash = transactionsHash;
        this.stateRootHash = stateRootHash;
        this.timestamp = timestamp;
        this.coinbase = coinbase;
    }

    public long getNumber() {
        return this.number;
    }

    public long getTimestamp() { return this.timestamp; }

    public Address getCoinbase() { return this.coinbase; }

    public BlockHash getHash() {
        if (this.hash == null)
            this.hash = this.calculateHash();

        return this.hash;
    }

    public BlockHash getParentHash() {
        return this.parentHash;
    }

    public Hash getTransactionsHash() { return this.transactionsHash; }

    public Hash getStateRootHash() {
        return this.stateRootHash;
    }

    private BlockHash calculateHash() {
        return new BlockHash(HashUtils.calculateHash(BlockHeaderEncoder.encode(this)));
    }
}
