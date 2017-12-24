package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.encoding.BlockHeaderEncoder;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import com.ajlopez.blockchain.utils.HashUtils;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockHeader {
    private static Hash emptyHash = new Hash(new byte[32]);

    private long number;
    private Hash parentHash;
    private Hash hash;
    private Hash transactionsHash;

    public BlockHeader(long number, Hash parentHash, Hash transactionsHash) {
        if (number < 0)
            throw new IllegalStateException("Negative number in block header");

        this.number = number;
        this.parentHash = parentHash == null ? emptyHash : parentHash;
        this.transactionsHash = transactionsHash;
    }

    public long getNumber() {
        return this.number;
    }

    public Hash getHash() {
        if (this.hash == null)
            this.hash = this.calculateHash();

        return this.hash;
    }

    public Hash getParentHash() {
        return this.parentHash;
    }

    public Hash getTransactionsHash() { return this.transactionsHash; }

    private Hash calculateHash() {
        return HashUtils.calculateHash(BlockHeaderEncoder.encode(this));
    }
}
