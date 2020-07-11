package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Difficulty;

/**
 * Created by ajlopez on 11/07/2020.
 */
public class ExtendedBlockInformation extends BlockInformation {
    private final Block block;
    private final long blockNumber;

    public ExtendedBlockInformation(Block block, Difficulty totalDifficulty) {
        super(block.getHash(), totalDifficulty);

        this.block = block;
        this.blockNumber = block.getNumber();
    }

    public Block getBlock() {
        return this.block;
    }

    public long getBlockNumber() {
        return this.blockNumber;
    }
}
