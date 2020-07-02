package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;

import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class TransactionProcessor {
    private final TransactionPool transactionPool;
    private final TransactionValidator transactionValidator = new TransactionValidator();

    public TransactionProcessor(TransactionPool transactionPool) {
        this.transactionPool = transactionPool;
    }

    public List<Transaction> processTransaction(Transaction transaction) {
        if (!this.transactionValidator.isValid(transaction))
            return Collections.emptyList();

        return this.transactionPool.addTransaction(transaction);
    }
}
