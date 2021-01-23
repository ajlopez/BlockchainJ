package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.test.utils.FactoryHelper;

/**
 * Created by ajlopez on 23/01/2021.
 */
public class BlockBuilder {
    private long number;

    public BlockBuilder number(long number) {
        this.number = number;

        return this;
    }

    public Block build() {
        return new Block(FactoryHelper.createBlockHeader(this.number), null, null);
    }
}
