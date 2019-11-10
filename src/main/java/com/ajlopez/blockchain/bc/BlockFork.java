package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 14/10/2019.
 */
public class BlockFork {
    private final List<Block> oldBlocks;
    private final List<Block> newBlocks;

    public BlockFork(List<Block> oldBlocks, List<Block> newBlocks) {
        this.oldBlocks = oldBlocks;
        this.newBlocks = newBlocks;
    }

    public List<Block> getOldBlocks() { return this.oldBlocks; }

    public List<Block> getNewBlocks() { return this.newBlocks; }

    public List<Transaction> getOldTransactions() {
        List<Transaction> result = new ArrayList<>();

        for (Block block : this.oldBlocks)
            for (Transaction transaction : block.getTransactions())
                result.add(transaction);

        return result;
    }

    public List<Transaction> getNewTransactions() {
        List<Transaction> result = new ArrayList<>();

        for (Block block : this.newBlocks)
            for (Transaction transaction : block.getTransactions())
                result.add(transaction);

        return result;
    }

    public static BlockFork fromBlocks(BlockChain blockChain, Block oldBestBlock, Block newBestBlock) {
        List<Block> oldBlocks = new ArrayList<>();
        List<Block> newBlocks = new ArrayList<>();

        Block oldBlock = oldBestBlock;
        Block newBlock = newBestBlock;

        while (true) {
            if (oldBlock == null && newBlock == null)
                break;

            if (oldBlock != null && newBlock != null && oldBlock.getHash().equals(newBlock.getHash()))
                break;

            if (oldBlock != null && (newBlock == null || oldBlock.getNumber() >= newBlock.getNumber())) {
                oldBlocks.add(oldBlock);
                oldBlock = blockChain.getBlockByHash(oldBlock.getParentHash());
            }
            else if (oldBlock == null || newBlock.getNumber() >= oldBlock.getNumber()) {
                newBlocks.add(newBlock);
                newBlock = blockChain.getBlockByHash(newBlock.getParentHash());
            }
        }

        return new BlockFork(oldBlocks, newBlocks);
    }
}
