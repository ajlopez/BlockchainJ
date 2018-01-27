package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class TransactionProcessor {
    TransactionPool transactionPool;

    public TransactionProcessor(TransactionPool transactionPool) {
        this.transactionPool = transactionPool;
    }

    public void processTransaction(Transaction transaction) {
        this.transactionPool.addTransaction(transaction);
    }
}
