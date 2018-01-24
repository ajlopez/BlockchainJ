package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessor {
    public Block mineBlock(Block parent, TransactionPool txpool) {
        return new Block(parent, txpool.getTransactions());
    }
}
