package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.merkle.MerkleTreeBuilder;

import java.util.ArrayList;
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

    public Block(long number, BlockHash parentHash, List<BlockHeader> uncles, List<Transaction> transactions, Hash stateRootHash, long timestamp, Address coinbase, Difficulty difficulty) {
        this(new BlockHeader(number, parentHash, transactions == null ? 0 : transactions.size(), calculateTransactionsRootHash(transactions), null, uncles == null ? 0 : uncles.size(), calculateUnclesRootHash(uncles), stateRootHash, timestamp, coinbase, difficulty, 0), uncles, transactions);
    }

    public Block(BlockHeader header, List<BlockHeader> uncles, List<Transaction> transactions)
    {
        this.header = header;

        if (uncles == null)
            this.uncles = Collections.EMPTY_LIST;
        else
            this.uncles = Collections.unmodifiableList(new ArrayList<>(uncles));

        if (transactions == null)
            this.transactions = Collections.EMPTY_LIST;
        else
            this.transactions = Collections.unmodifiableList(new ArrayList<>(transactions));
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

    public int getTransactionsCount() { return this.header.getTransactionsCount(); }

    public Hash getTransactionsRootHash() {
        return this.header.getTransactionsRootHash();
    }

    public int getUnclesCount() { return this.header.getUnclesCount(); }

    public Hash getUnclesRootHash() {
        return this.header.getUnclesRootHash();
    }

    public long getTimestamp() { return this.header.getTimestamp(); }

    public Difficulty getDifficulty() { return this.header.getDifficulty(); }

    public Difficulty getCummulativeDifficulty() {
        Difficulty result = this.getDifficulty();

        for (BlockHeader uncle : this.uncles)
            result = result.add(uncle.getDifficulty());

        return result;
    }

    public List<BlockHeader> getUncles() { return this.uncles; }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public static Hash calculateTransactionsRootHash(List<Transaction> transactions) {
        MerkleTreeBuilder merkleTreeBuilder = new MerkleTreeBuilder();

        if (transactions == null)
            return merkleTreeBuilder.build().getHash();

        for (Transaction transaction : transactions)
            merkleTreeBuilder.add(transaction.getHash());

        return merkleTreeBuilder.build().getHash();
    }

    public static Hash calculateUnclesRootHash(List<BlockHeader> uncles) {
        MerkleTreeBuilder merkleTreeBuilder = new MerkleTreeBuilder();

        if (uncles == null)
            return merkleTreeBuilder.build().getHash();

        for (BlockHeader uncle : uncles)
            merkleTreeBuilder.add(uncle.getHash());

        return merkleTreeBuilder.build().getHash();
    }
}

