package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;

import java.util.*;

/**
 * Created by ajlopez on 21/01/2018.
 */
public class TransactionPool {
    private static List<Transaction> emptyList = Collections.unmodifiableList(Arrays.asList());

    private Set<Transaction> transactions = new HashSet<>();

    public List<Transaction> addTransaction(Transaction transaction) {
        if (transaction == null)
            throw new IllegalArgumentException("Null transaction");

        synchronized (this.transactions) {
            if (!this.transactions.add(transaction))
                return emptyList;
        }

        return Collections.singletonList(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        if (transaction == null)
            throw new IllegalArgumentException("Null transaction");

        synchronized (this.transactions) {
            this.transactions.remove(transaction);
        }
    }

    public List<Transaction> getTransactions() {
        List<Transaction> list = new ArrayList<>();

        synchronized (this.transactions) {
            list.addAll(this.transactions);
        }

        return list;
    }

    public List<Transaction> getTransactionsWithSender(Address sender) {
        List<Transaction> list = new ArrayList<>();

        synchronized (this.transactions) {
            for (Transaction transaction : this.transactions)
                if (transaction.getSender().equals(sender))
                    list.add(transaction);
        }

        return list;
    }
}

