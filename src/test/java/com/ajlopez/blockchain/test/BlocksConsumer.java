package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.core.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 02/05/2020.
 */
public class BlocksConsumer implements Consumer<Block> {
    private final List<Block> blocks = new ArrayList<>();

    @Override
    public void accept(Block block) {
        this.blocks.add(block);
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }
}
