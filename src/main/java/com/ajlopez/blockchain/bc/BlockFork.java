package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;

import java.io.IOException;
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
        return getTransactionsFromBlocks(this.oldBlocks);
    }

    public List<Transaction> getNewTransactions() {
        return getTransactionsFromBlocks(this.newBlocks);
    }

    private static List<Transaction> getTransactionsFromBlocks(List<Block> blocks) {
        List<Transaction> result = new ArrayList<>();

        for (Block block : blocks)
            for (Transaction transaction : block.getTransactions())
                result.add(transaction);

        return result;
    }

    public static BlockFork fromBlocks(BlockChain blockChain, Block oldBestBlock, Block newBestBlock) throws IOException {
        List<Block> oldBlocks = new ArrayList<>();
        List<Block> newBlocks = new ArrayList<>();

        Block oldBlock = oldBestBlock;
        Block newBlock = newBestBlock;

        while (!sameBlock(oldBlock, newBlock)) {
            if (isPreviousBlock(newBlock, oldBlock)) {
                oldBlocks.add(oldBlock);
                oldBlock = blockChain.getBlockByHash(oldBlock.getParentHash());
            }
            else {
                newBlocks.add(newBlock);
                newBlock = blockChain.getBlockByHash(newBlock.getParentHash());
            }
        }

        return new BlockFork(oldBlocks, newBlocks);
    }

    private static boolean isPreviousBlock(Block block1, Block block2) {
        if (block1 == null)
            return true;

        if (block2 == null)
            return false;

        return block1.getNumber() < block2.getNumber();
    }

    private static boolean sameBlock(Block block1, Block block2) {
        if (block1 == null && block2 == null)
            return true;

        if (block1 != null && block2 != null && block1.getHash().equals(block2.getHash()))
            return true;

        return false;
    }
}
