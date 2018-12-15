package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import com.ajlopez.blockchain.utils.HashUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Block {
    private final BlockHeader header;
    private final List<Transaction> transactions;

    public Block(Block parent, List<Transaction> txs, Hash stateRootHash, long timestamp) {
        this(parent.getNumber() + 1, parent.getHash(), txs, stateRootHash, timestamp);
    }

    public Block(long number, BlockHash parentHash, Hash stateRootHash, long timestamp) {
        this(number, parentHash, new ArrayList<>(), stateRootHash, timestamp);
    }

    public Block(long number, BlockHash parentHash, List<Transaction> txs, Hash stateRootHash, long timestamp) {
        this(new BlockHeader(number, parentHash, HashUtils.calculateHash(TransactionEncoder.encode(txs)), stateRootHash, timestamp), txs);
    }

    public Block(BlockHeader header, List<Transaction> transactions)
    {
        this.header = header;

        if (transactions == null)
            this.transactions = new ArrayList<>();
        else
            this.transactions = transactions;
    }

    public BlockHeader getHeader() {
        return this.header;
    }

    public long getNumber() {
        return this.header.getNumber();
    }

    public BlockHash getHash() {
        return this.header.getHash();
    }

    public BlockHash getParentHash() {
        return this.header.getParentHash();
    }

    public Hash getStateRootHash() {
        return this.header.getStateRootHash();
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }
}
