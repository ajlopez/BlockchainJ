package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;

/**
 * Created by ajlopez on 09/03/2020.
 */
public class BlockInformation {
    private final BlockHash blockHash;
    private final Difficulty totalDifficulty;

    public BlockInformation(BlockHash blockHash, Difficulty totalDifficulty) {
        this.blockHash = blockHash;
        this.totalDifficulty = totalDifficulty;
    }

    public BlockHash getBlockHash() { return this.blockHash; }

    public Difficulty getTotalDifficulty() { return this.totalDifficulty; }
}
