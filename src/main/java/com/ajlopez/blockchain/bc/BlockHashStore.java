package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Hash;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 03/02/2018.
 */
public class BlockHashStore {
    private Map<Hash, Block> blocks = new HashMap<>();

    public void saveBlock(Block block) {
        this.blocks.put(block.getHash(), block);
    }

    public Block getBlock(Hash hash) {
        return this.blocks.get(hash);
    }

    public boolean containsBlock(Hash hash) {
        return this.blocks.containsKey(hash);
    }
}
