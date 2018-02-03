package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Hash;

/**
 * Created by ajlopez on 15/08/2017.
 */
public class BlockChain {
    private Block best;
    private BlockHashStore blocksByHash = new BlockHashStore();
    private BlockNumberStore blocksByNumber = new BlockNumberStore();

    public Block getBestBlock() {
        return best;
    }

    public boolean connectBlock(Block block) {
        if (isOrphan(block))
            return false;

        if (this.blocksByHash.containsBlock(block.getHash()))
            return this.blocksByNumber.containsBlock(block);

        this.saveBlock(block);

        if (this.best == null || block.getNumber() > this.best.getNumber())
            this.saveBestBlock(block);

        return true;
    }

    public Block getBlockByHash(Hash hash) {
        return this.blocksByHash.getBlock(hash);
    }

    public boolean isChainedBlock(Hash hash) {
        return this.blocksByHash.containsBlock(hash);
    }

    public Block getBlockByNumber(long number) {
        return this.blocksByNumber.getBlock(number);
    }

    private boolean isOrphan(Block block) {
        if (block.getNumber() == 0)
            return false;

        return !blocksByHash.containsBlock(block.getParentHash());
    }

    private void saveBlock(Block block) {
        if (!this.blocksByHash.containsBlock(block.getHash()))
            this.blocksByHash.saveBlock(block);
    }

    private void saveBestBlock(Block block) {
        this.best = block;

        this.blocksByNumber.saveBlock(block);

        while (block.getNumber() > 0 && !this.blocksByNumber.getBlock(block.getNumber() - 1).getHash().equals(block.getParentHash())) {
            block = this.blocksByHash.getBlock(block.getParentHash());
            this.blocksByNumber.saveBlock(block);
        }
    }
}
