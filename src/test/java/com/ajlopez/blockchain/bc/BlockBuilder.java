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
    private BlockHeader parentHeader;
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

    public BlockBuilder parentHeader(BlockHeader parentHeader) {
        this.parentHeader = parentHeader;

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
        return new Block(this.buildHeader(), this.uncles, this.transactions);
    }

    public BlockHeader buildHeader() {
        BlockHash parentHashToUse;
        long numberToUse;

        if (this.parent != null) {
            parentHashToUse = this.parent.getHash();
            numberToUse = this.parent.getNumber() + 1;
        }
        else if (this.parentHeader != null) {
            parentHashToUse = this.parentHeader.getHash();
            numberToUse = this.parentHeader.getNumber() + 1;
        }
        else {
            numberToUse = this.number;

            if (this.parentHash == null)
                parentHashToUse = numberToUse == 0 ? BlockHash.EMPTY_BLOCK_HASH : FactoryHelper.createRandomBlockHash();
            else
                parentHashToUse = this.parentHash;
        }

        return createBlockHeader(parentHashToUse, numberToUse, this.transactions, this.uncles);
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
}
