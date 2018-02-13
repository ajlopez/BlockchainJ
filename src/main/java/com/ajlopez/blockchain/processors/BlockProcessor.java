package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.types.Hash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 17/12/2017.
 */
public class BlockProcessor {
    private OrphanBlocks orphanBlocks;
    private BlockChain blockChain;

    public BlockProcessor(BlockChain blockChain, OrphanBlocks orphanBlocks) {
        this.blockChain = blockChain;
        this.orphanBlocks = orphanBlocks;
    }

    public void processBlock(Block block) {
        Hash hash = block.getHash();

        if (this.orphanBlocks.isKnownOrphan(hash))
            return;

        if (this.blockChain.isChainedBlock(hash))
            return;

        if (blockChain.connectBlock(block))
            connectDescendants(block);
        else
            orphanBlocks.addToOrphans(block);
    }

    public Block getBestBlock() {
        return this.blockChain.getBestBlock();
    }

    public long getBestBlockNumber() {
        return this.blockChain.getBestBlockNumber();
    }

    public Block getBlockByHash(Hash hash) {
        return this.blockChain.getBlockByHash(hash);
    }

    public Block getBlockByNumber(long number) {
        return this.blockChain.getBlockByNumber(number);
    }

    public boolean isChainedBlock(Hash hash) {
        return this.blockChain.isChainedBlock(hash);
    }

    public boolean isOrphanBlock(Hash hash) {
        return this.orphanBlocks.isKnownOrphan(hash);
    }

    public boolean isKnownBlock(Hash hash) {
        return this.isChainedBlock(hash) || this.isOrphanBlock(hash);
    }

    private void connectDescendants(Block block) {
        List<Block> children = new ArrayList<>(orphanBlocks.getChildrenOrphanBlocks(block));

        children.forEach(child -> {
            orphanBlocks.removeOrphan(child);
            processBlock(child);
        });
    }
}
