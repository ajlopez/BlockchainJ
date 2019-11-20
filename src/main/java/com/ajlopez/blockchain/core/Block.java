package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Block {
    private final BlockHeader header;
    private final List<BlockHeader> uncles;
    private final List<Transaction> transactions;

    public Block(Block parent, List<BlockHeader> uncles, List<Transaction> transactions, Hash stateRootHash, long timestamp, Address coinbase, Difficulty difficulty) {
        this(parent.getNumber() + 1, parent.getHash(), uncles, transactions, stateRootHash, timestamp, coinbase, difficulty);
    }

    public Block(long number, BlockHash parentHash, Hash stateRootHash, long timestamp, Address coinbase, Difficulty difficulty) {
        this(number, parentHash, Collections.emptyList(), Collections.emptyList(), stateRootHash, timestamp, coinbase, difficulty);
    }

    public Block(long number, BlockHash parentHash, List<BlockHeader> uncles, List<Transaction> txs, Hash stateRootHash, long timestamp, Address coinbase, Difficulty difficulty) {
        this(new BlockHeader(number, parentHash, calculateTransactionsRootHash(txs), stateRootHash, timestamp, coinbase, difficulty), null, txs);
    }

    public Block(BlockHeader header, List<BlockHeader> uncles, List<Transaction> transactions)
    {
        this.header = header;

        if (uncles == null)
            this.uncles = Collections.EMPTY_LIST;
        else
            this.uncles = uncles;

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

    public List<BlockHeader> getUncles() { return this.uncles; }

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
