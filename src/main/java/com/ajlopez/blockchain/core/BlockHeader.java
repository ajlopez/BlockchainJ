package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.BlockHeaderEncoder;
import com.ajlopez.blockchain.utils.HashUtils;

import java.util.Arrays;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockHeader {
    private final long number;
    private final BlockHash parentHash;
    private final Hash transactionsRootHash;
    private final Hash stateRootHash;
    private final long timestamp;
    private final Address coinbase;
    private final Difficulty difficulty;

    private BlockHash hash;

    public BlockHeader(long number, BlockHash parentHash, Hash transactionsRootHash, Hash stateRootHash, long timestamp, Address coinbase, Difficulty difficulty) {
        if (number < 0)
            throw new IllegalStateException("Negative number in block header");

        this.number = number;
        this.parentHash = parentHash == null ? BlockHash.EMPTY_BLOCK_HASH : parentHash;
        this.transactionsRootHash = transactionsRootHash;
        this.stateRootHash = stateRootHash;
        this.timestamp = timestamp;
        this.coinbase = coinbase;
        this.difficulty = difficulty;
    }

    public long getNumber() {
        return this.number;
    }

    public long getTimestamp() { return this.timestamp; }

    public Address getCoinbase() { return this.coinbase; }

    public Difficulty getDifficulty() { return this.difficulty; }

    public BlockHash getHash() {
        if (this.hash == null)
            this.hash = this.calculateHash();

        return this.hash;
    }

    public BlockHash getParentHash() {
        return this.parentHash;
    }

    public Hash getTransactionsRootHash() { return this.transactionsRootHash; }

    public Hash getStateRootHash() {
        return this.stateRootHash;
    }

    private BlockHash calculateHash() {
        return new BlockHash(HashUtils.keccak256(BlockHeaderEncoder.encode(this)));
    }

    @Override
    public int hashCode() {
        return this.getHash().asInteger();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof BlockHeader))
            return false;

        BlockHeader bh = (BlockHeader)obj;

        return Arrays.equals(BlockHeaderEncoder.encode(this), BlockHeaderEncoder.encode(bh));
    }
}
