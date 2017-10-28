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
    private Map<Hash, Block> orphansByHash = new HashMap<>();
    private Map<Hash, List<Block>> orphansByParent = new HashMap<>();

    public Block getBestBlock() {
        return best;
    }

    public void connectBlock(Block block) {
        if (isKnownOrphan(block))
            return;

        if (isOrphan(block)) {
            addToOrphans(block);
            return;
        }

        saveBlock(block);

        if (this.best == null || block.getNumber() > this.best.getNumber())
            this.best = block;

        connectDescendants(block);
    }

    private boolean isKnownOrphan(Block block) {
        return this.orphansByHash.containsKey(block.getHash());
    }

    private boolean isOrphan(Block block) {
        if (block.getNumber() == 0)
            return false;

        return !blocksByHash.containsKey(block.getParentHash());
    }

    private void addToOrphans(Block block) {
        this.orphansByHash.put(block.getHash(), block);

        Hash parentHash = block.getParentHash();

        if (!this.orphansByParent.containsKey(parentHash))
            this.orphansByParent.put(parentHash, new ArrayList<>());

        this.orphansByParent.get(parentHash).add(block);
    }

    private void saveBlock(Block block) {
        if (!this.blocksByHash.containsKey(block.getHash()))
            this.blocksByHash.put(block.getHash(), block);
    }

    private void connectDescendants(Block block) {
        List<Block> children = new ArrayList<>(getChildrenOrphanBlocks(block));

        children.forEach(child -> {
            removeOrphan(child);
            connectBlock(child);
        });
    }

    private void removeOrphan(Block block) {
        this.orphansByHash.remove(block.getHash());

        List<Block> siblings = this.orphansByParent.get(block.getParentHash());

        siblings.remove(block);

        if (siblings.isEmpty())
            this.orphansByParent.remove(block.getParentHash());
    }

    private List<Block> getChildrenOrphanBlocks(Block block) {
        List<Block> children = this.orphansByParent.get(block.getHash());

        if (children == null)
            return new ArrayList<>();

        return children;
    }
}
