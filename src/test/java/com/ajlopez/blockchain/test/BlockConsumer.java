package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.core.Block;

import java.util.function.Consumer;

/**
 * Created by ajlopez on 20/02/2018.
 */
public class BlockConsumer implements Consumer<Block> {
    private Block block;

    @Override
    public void accept(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }
}
