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

    public long getTransactionNonceBySenderFromNonce(Address sender, long fromNonce) {
        List<Transaction> list = getTransactionsWithSenderFromNonce(sender, fromNonce);

        if (list.isEmpty())
            return fromNonce;

        return list.get(list.size() - 1).getNonce() + 1;
    }

    public List<Transaction> getTransactionsWithSenderFromNonce(Address sender, long firstNonce) {
        List<Transaction> list = new ArrayList<>();

        synchronized (this.transactions) {
            for (Transaction transaction : this.transactions)
                if (transaction.getSender().equals(sender) && transaction.getNonce() >= firstNonce)
                    list.add(transaction);
        }

        Collections.sort(list, new NonceComparator());

        List<Transaction> result = new ArrayList<>();
        long expectedNonce = firstNonce;

        for (Transaction transaction : list) {
            if (transaction.getNonce() > expectedNonce)
                break;

            result.add(transaction);

            expectedNonce = transaction.getNonce() + 1;
        }

        return result;
    }

    class NonceComparator implements Comparator<Transaction> {
        public int compare(Transaction t1, Transaction t2) {
            return (int)(t1.getNonce() - t2.getNonce());
        }
    }
}

