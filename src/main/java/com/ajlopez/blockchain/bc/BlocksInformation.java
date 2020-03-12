package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 11/03/2020.
 */
public class BlocksInformation {
    private int blockOnChainPosition = -1;
    private final List<BlockInformation> blocksInformation = new ArrayList<>();

    public BlockInformation getBlockOnChain() {
        if (this.blockOnChainPosition < 0)
            return null;

        return this.blocksInformation.get(this.blockOnChainPosition);
    }

    public void addBlockInformation(BlockHash blockHash, Difficulty totalDifficulty) {
        BlockInformation blockInformation = new BlockInformation(blockHash, totalDifficulty);

        this.blocksInformation.add(blockInformation);
    }

    public void setBlockOffChain(BlockHash blockHash) {
        if (this.blockOnChainPosition >= 0 && this.blocksInformation.get(this.blockOnChainPosition).getBlockHash().equals(blockHash))
            this.blockOnChainPosition = -1;
    }

    public void setBlockOnChain(BlockHash blockHash) {
        int nb = this.blocksInformation.size();

        for (int k = 0; k < nb; k++)
            if (this.blocksInformation.get(k).getBlockHash().equals(blockHash)) {
                this.blockOnChainPosition = k;

                return;
            }

        // TODO not found case
    }

    public BlockInformation getBlockInformation(BlockHash blockHash) {
        int nb = this.blocksInformation.size();

        for (int k = 0; k < nb; k++) {
            BlockInformation blockInformation = this.blocksInformation.get(k);

            if (blockHash.equals(blockInformation.getBlockHash()))
                return blockInformation;
        }

        return null;
    }
}
