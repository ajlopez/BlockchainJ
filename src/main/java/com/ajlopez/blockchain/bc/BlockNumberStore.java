package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 03/02/2018.
 */
public class BlockNumberStore {
    private Map<Long, Block> blocks = new HashMap<>();

    public void saveBlock(Block block) {
        this.blocks.put(block.getNumber(), block);
    }

    public Block getBlock(long number) {
        return this.blocks.get(number);
    }

    public boolean containsBlock(Block block) {
        Block b = this.blocks.get(block.getNumber());

        if (b == null)
            return false;

        return block.getHash().equals(b.getHash());
    }
}
