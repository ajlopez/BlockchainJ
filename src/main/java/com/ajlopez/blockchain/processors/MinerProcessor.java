package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;

import java.util.List;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessor {
    public Block mineBlock(Block parent, TransactionPool txpool) {
        List<Transaction> txs = txpool.getTransactions();

        Block block = new Block(parent, txpool.getTransactions());

        txpool.removeTransactions(txs);

        return block;
    }
}
