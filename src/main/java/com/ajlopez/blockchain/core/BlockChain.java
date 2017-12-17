package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.processors.OrphanBlocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 15/08/2017.
 */
public class BlockChain {
    private Block best;
    private Map<Hash, Block> blocksByHash = new HashMap<>();

    public Block getBestBlock() {
        return best;
    }

    public boolean connectBlock(Block block) {
        if (isOrphan(block))
            return false;

        this.saveBlock(block);

        if (this.best == null || block.getNumber() > this.best.getNumber())
            this.best = block;

        return true;
    }

    private boolean isOrphan(Block block) {
        if (block.getNumber() == 0)
            return false;

        return !blocksByHash.containsKey(block.getParentHash());
    }

    private void saveBlock(Block block) {
        if (!this.blocksByHash.containsKey(block.getHash()))
            this.blocksByHash.put(block.getHash(), block);
    }
}
