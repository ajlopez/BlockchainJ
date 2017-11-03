package com.ajlopez.blockchain.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Block {
    private BlockHeader header;
    private List<Transaction> transactions;

    public Block(long number, Hash parentHash) {
        this(new BlockHeader(number, parentHash));
    }

    public Block(long number, Hash parentHash, List<Transaction> txs) {
        this(new BlockHeader(number, parentHash), txs);
    }

    public Block(BlockHeader header) {
        this(header, null);
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

    public Hash getHash() {
        return this.header.getHash();
    }

    public Hash getParentHash() {
        return this.header.getParentHash();
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }
}
