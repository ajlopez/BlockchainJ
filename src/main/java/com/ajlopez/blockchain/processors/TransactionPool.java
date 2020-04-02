package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by ajlopez on 21/01/2018.
 */
public class TransactionPool {
    private final Set<Transaction> transactions = new HashSet<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public List<Transaction> addTransaction(Transaction transaction) {
        if (transaction == null)
            throw new IllegalArgumentException("Null transaction");

        this.lock.writeLock().lock();

        try {
            if (!this.transactions.add(transaction))
                return Collections.emptyList();
        }
        finally {
            this.lock.writeLock().unlock();
        }

        return Collections.singletonList(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        if (transaction == null)
            throw new IllegalArgumentException("Null transaction");

        this.lock.writeLock().lock();

        try {
            this.transactions.remove(transaction);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public List<Transaction> getTransactions() {
        List<Transaction> list = new ArrayList<>();

        this.lock.readLock().lock();

        try {
            list.addAll(this.transactions);
        }
        finally {
            this.lock.readLock().unlock();
        }

        return list;
    }

    public List<Transaction> getTransactionsWithSender(Address sender) {
        List<Transaction> list = new ArrayList<>();

        this.lock.readLock().lock();

        try {
            for (Transaction transaction : this.transactions)
                if (transaction.getSender().equals(sender))
                    list.add(transaction);
        }
        finally {
            this.lock.readLock().unlock();
        }

        return list;
    }

    public long getTransactionNonceBySenderFromNonce(Address sender, long fromNonce) {
        List<Transaction> list = this.getTransactionsWithSenderFromNonce(sender, fromNonce);

        if (list.isEmpty())
            return fromNonce;

        return list.get(list.size() - 1).getNonce() + 1;
    }

    public List<Transaction> getTransactionsWithSenderFromNonce(Address sender, long firstNonce) {
        List<Transaction> list = new ArrayList<>();

        this.lock.readLock().lock();

        try {
            for (Transaction transaction : this.transactions)
                if (transaction.getSender().equals(sender) && transaction.getNonce() >= firstNonce)
                    list.add(transaction);
        }
        finally {
            this.lock.readLock().unlock();
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

    public void updateTransactions(List<Transaction> toremove, List<Transaction> toadd) {
        this.lock.writeLock().lock();

        try {
            for (Transaction tx : toremove)
                this.removeTransaction(tx);

            for (Transaction tx : toadd)
                this.addTransaction(tx);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    class NonceComparator implements Comparator<Transaction> {
        public int compare(Transaction t1, Transaction t2) {
            return (int)(t1.getNonce() - t2.getNonce());
        }
    }
}

