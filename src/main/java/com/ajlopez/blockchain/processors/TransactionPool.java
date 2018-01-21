package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;

import java.util.*;

/**
 * Created by ajlopez on 21/01/2018.
 */
public class TransactionPool {
    private Set<Transaction> transactions = new HashSet<>();

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        List<Transaction> list = new ArrayList<Transaction>();

        list.addAll(this.transactions);

        return list;
    }
}
