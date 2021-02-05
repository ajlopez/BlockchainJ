package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;

import java.util.List;

/**
 * Created by ajlopez on 23/01/2021.
 */
public class BlockBuilder {
    private long number;
    private Block parent;
    private BlockHash parentHash;
    private List<Transaction> transactions;
    private List<BlockHeader> uncles;

    public BlockBuilder number(long number) {
        this.number = number;

        return this;
    }

    public BlockBuilder parent(Block parent) {
        this.parent = parent;

        return this;
    }

    public BlockBuilder parentHash(BlockHash parentHash) {
        this.parentHash = parentHash;

        return this;
    }

    public BlockBuilder transactions(List<Transaction> transactions) {
        this.transactions = transactions;

        return this;
    }

    public BlockBuilder uncles(List<BlockHeader> uncles) {
        this.uncles = uncles;

        return this;
    }

    public Block build() {
        if (this.parentHash != null)
            return new Block(createBlockHeader(this.parentHash, this.number, this.transactions, this.uncles), this.uncles, this.transactions);

        if (this.parent != null)
            return new Block(createBlockHeader(this.parent, this.transactions, this.uncles), this.uncles, this.transactions);

        return new Block(createBlockHeader(this.number, this.transactions, this.uncles), this.uncles, this.transactions);
    }

    public static BlockHeader createBlockHeader(BlockHash parentHash, long number, List<Transaction> transactions, List<BlockHeader> uncles) {
        Hash transactionsHash = Block.calculateTransactionsRootHash(transactions);
        Hash unclesHash = Block.calculateUnclesRootHash(uncles);

        int ntransactions = transactions == null ? 0 : transactions.size();
        int nuncles = uncles == null ? 0 : uncles.size();

        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        Difficulty difficulty = Difficulty.fromUnsignedLong(42);

        return new BlockHeader(number, parentHash, ntransactions, transactionsHash, null, nuncles, unclesHash, stateRootHash, System.currentTimeMillis() / 1000, coinbase, difficulty, 12_000_000L, 0, null, 0, 0);
    }

    public static BlockHeader createBlockHeader(Block parent, List<Transaction> transactions, List<BlockHeader> uncles) {
        Hash transactionsHash = Block.calculateTransactionsRootHash(transactions);
        Hash unclesHash = Block.calculateUnclesRootHash(uncles);
        int ntransactions = transactions == null ? 0 : transactions.size();
        int nuncles = uncles == null ? 0 : uncles.size();

        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        Difficulty difficulty = Difficulty.fromUnsignedLong(42);

        return new BlockHeader(parent.getNumber() + 1, parent.getHash(), ntransactions, transactionsHash, null, nuncles, unclesHash, stateRootHash, System.currentTimeMillis() / 1000, coinbase, difficulty, 12_000_000L, 0, null, 0, 0);
    }

    public static BlockHeader createBlockHeader(long blockNumber, List<Transaction> transactions, List<BlockHeader> uncles) {
        BlockHash hash = blockNumber == 0 ? BlockHash.EMPTY_BLOCK_HASH : FactoryHelper.createRandomBlockHash();
        int ntransactions = transactions == null ? 0 : transactions.size();
        int nuncles = uncles == null ? 0 : uncles.size();

        Hash transactionsHash = Block.calculateTransactionsRootHash(transactions);
        Hash unclesHash = Block.calculateUnclesRootHash(uncles);

        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        Difficulty difficulty = Difficulty.fromUnsignedLong(42);

        return new BlockHeader(blockNumber, hash, ntransactions, transactionsHash, null, nuncles, unclesHash, stateRootHash, System.currentTimeMillis() / 1000, coinbase, difficulty, 12_000_000L, 0, null, 0, 0);
    }
}
