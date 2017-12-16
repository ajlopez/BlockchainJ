package com.ajlopez.blockchain.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 16/12/2017.
 */
public class OrphanBlocks {
    private Map<Hash, Block> orphansByHash = new HashMap<>();
    private Map<Hash, List<Block>> orphansByParent = new HashMap<>();

    public boolean isKnownOrphan(Block block) {
        return this.orphansByHash.containsKey(block.getHash());
    }

    public void addToOrphans(Block block) {
        this.orphansByHash.put(block.getHash(), block);

        Hash parentHash = block.getParentHash();

        if (!this.orphansByParent.containsKey(parentHash))
            this.orphansByParent.put(parentHash, new ArrayList<>());

        this.orphansByParent.get(parentHash).add(block);
    }

    public List<Block> getChildrenOrphanBlocks(Block block) {
        List<Block> children = this.orphansByParent.get(block.getHash());

        if (children == null)
            return new ArrayList<>();

        return children;
    }

    public void removeOrphan(Block block) {
        this.orphansByHash.remove(block.getHash());

        List<Block> siblings = this.orphansByParent.get(block.getParentHash());

        siblings.remove(block);

        if (siblings.isEmpty())
            this.orphansByParent.remove(block.getParentHash());
    }
}
