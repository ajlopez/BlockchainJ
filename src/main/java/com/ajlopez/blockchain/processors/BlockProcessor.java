package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockChain;

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
        if (this.orphanBlocks.isKnownOrphan(block))
            return;

        if (blockChain.connectBlock(block))
            connectDescendants(block);
        else
            orphanBlocks.addToOrphans(block);
    }

    public Block getBestBlock() {
        return this.blockChain.getBestBlock();
    }

    private void connectDescendants(Block block) {
        List<Block> children = new ArrayList<>(orphanBlocks.getChildrenOrphanBlocks(block));

        children.forEach(child -> {
            orphanBlocks.removeOrphan(child);
            processBlock(child);
        });
    }
}
