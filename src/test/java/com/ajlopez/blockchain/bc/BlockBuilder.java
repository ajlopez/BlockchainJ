package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;

import java.util.List;

/**
 * Created by ajlopez on 23/01/2021.
 */
public class BlockBuilder {
    private long number;
    private Block parent;
    private BlockHash parentHash;
    private List<BlockHeader> uncles;

    public BlockBuilder number(long number) {
        this.number = number;

        return this;
    }

    public BlockBuilder parent(Block parent) {
        this.parent = parent;

        return this;
    }

    public BlockBuilder parentHash(BlockHash parentHash) {
        this.parentHash = parentHash;

        return this;
    }

    public BlockBuilder uncles(List<BlockHeader> uncles) {
        this.uncles = uncles;

        return this;
    }

    public Block build() {
        if (this.parentHash != null)
            return new Block(FactoryHelper.createBlockHeader(this.parentHash, this.number, null, this.uncles), this.uncles, null);

        if (this.parent != null)
            return new Block(FactoryHelper.createBlockHeader(this.parent, null, this.uncles), this.uncles, null);

        return new Block(FactoryHelper.createBlockHeader(this.number, null, this.uncles), this.uncles, null);
    }
}
