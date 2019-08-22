package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.core.types.TransactionHash;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.utils.HexUtils;

import java.util.List;

/**
 * Created by ajlopez on 20/08/2019.
 */
public class TransactionsProvider {
    private TransactionPool transactionPool;

    public TransactionsProvider(TransactionPool transactionPool) {
        this.transactionPool = transactionPool;
    }

    public Transaction getTransaction(String txid) {
        TransactionHash hash = new TransactionHash(HexUtils.hexStringToBytes(txid));

        // TODO improve, add map in transaction pool
        List<Transaction> transactions = this.transactionPool.getTransactions();

        for (Transaction transaction : transactions)
            if (transaction.getHash().equals(hash))
                return transaction;

        return null;
    }
}
