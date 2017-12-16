package com.ajlopez.blockchain.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ajlopez on 15/08/2017.
 */
public class BlockChain {
    private Block best;
    private Map<Hash, Block> blocksByHash = new HashMap<>();
    private OrphanBlocks orphanBlocks = new OrphanBlocks();

    public Block getBestBlock() {
        return best;
    }

    public void connectBlock(Block block) {
        if (this.orphanBlocks.isKnownOrphan(block))
            return;

        if (isOrphan(block)) {
            orphanBlocks.addToOrphans(block);
            return;
        }

        saveBlock(block);

        if (this.best == null || block.getNumber() > this.best.getNumber())
            this.best = block;

        connectDescendants(block);
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

    private void connectDescendants(Block block) {
        List<Block> children = new ArrayList<>(orphanBlocks.getChildrenOrphanBlocks(block));

        children.forEach(child -> {
            orphanBlocks.removeOrphan(child);
            connectBlock(child);
        });
    }
}
