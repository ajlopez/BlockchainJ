package com.ajlopez.blockchain.core;

import java.util.HashMap;
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

    public Block getBlockByHash(Hash hash) {
        return this.blocksByHash.get(hash);
    }

    public boolean isChainedBlock(Hash hash) {
        return this.blocksByHash.containsKey(hash);
    }

    public Block getBlockByNumber(long number) {
        for (Block b: this.blocksByHash.values())
            if (b.getNumber() == number)
                return b;

        return null;
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
