package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;

import java.util.List;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessor {
    private BlockProcessor blockProcessor;
    private TransactionPool transactionPool;

    public MinerProcessor(BlockProcessor blockProcessor, TransactionPool transactionPool) {
        this.blockProcessor = blockProcessor;
        this.transactionPool = transactionPool;
    }

    public void process() {
        Block bestBlock = this.blockProcessor.getBestBlock();
        Block newBlock = this.mineBlock(bestBlock, this.transactionPool);

        this.blockProcessor.processBlock(newBlock);
    }

    public Block mineBlock(Block parent, TransactionPool txpool) {
        return new Block(parent, txpool.getTransactions());
    }
}
