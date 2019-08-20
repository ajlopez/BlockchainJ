package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.processors.TransactionPool;

/**
 * Created by ajlopez on 20/08/2019.
 */
public class TransactionsProvider {
    private TransactionPool transactionPool;

    public TransactionsProvider(TransactionPool transactionPool) {
        this.transactionPool = transactionPool;
    }

    public Transaction getTransaction(String txid) {
        return null;
    }
}
