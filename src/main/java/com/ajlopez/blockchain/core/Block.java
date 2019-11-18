package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Block {
    private final BlockHeader header;
    private final List<Transaction> transactions;

    public Block(Block parent, List<Transaction> txs, Hash stateRootHash, long timestamp, Address coinbase, Difficulty difficulty) {
        this(parent.getNumber() + 1, parent.getHash(), txs, stateRootHash, timestamp, coinbase, difficulty);
    }

    public Block(long number, BlockHash parentHash, Hash stateRootHash, long timestamp, Address coinbase, Difficulty difficulty) {
        this(number, parentHash, Collections.emptyList(), stateRootHash, timestamp, coinbase, difficulty);
    }

    public Block(long number, BlockHash parentHash, List<Transaction> txs, Hash stateRootHash, long timestamp, Address coinbase, Difficulty difficulty) {
        this(new BlockHeader(number, parentHash, calculateTransactionsRootHash(txs), stateRootHash, timestamp, coinbase, difficulty), txs);
    }

    public Block(BlockHeader header, List<Transaction> transactions)
    {
        this.header = header;

        if (transactions == null)
            this.transactions = Collections.EMPTY_LIST;
        else
            this.transactions = Collections.unmodifiableList(transactions);
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

    public Address getCoinbase() { return this.header.getCoinbase(); }

    public BlockHash getParentHash() {
        return this.header.getParentHash();
    }

    public Hash getStateRootHash() {
        return this.header.getStateRootHash();
    }

    public Hash getTransactionRootHash() {
        return this.header.getTransactionsRootHash();
    }

    public long getTimestamp() { return this.header.getTimestamp(); }

    public Difficulty getDifficulty() { return this.header.getDifficulty(); }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public static Hash calculateTransactionsRootHash(List<Transaction> transactions) {
        Trie trie = new Trie();
        int ntransactions = transactions.size();

        for (int k = 0; k < ntransactions; k++)
            trie = trie.put(ByteUtils.unsignedIntegerToNormalizedBytes(k), TransactionEncoder.encode(transactions.get(k)));

        return trie.getHash();
    }
}
