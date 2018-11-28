package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.AccountStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class TransactionExecutor {
    private final AccountStore accountStore;
    private final TopExecutionContext topExecutionContext;

    public TransactionExecutor(AccountStore accountStore) {
        this.accountStore = accountStore;
        this.topExecutionContext = new TopExecutionContext(this.accountStore);
    }

    public Hash getHashRoot() {
        return this.accountStore.getRootHash();
    }

    public List<Transaction> executeTransactions(List<Transaction> transactions) {
        List<Transaction> executed = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getNonce() != this.topExecutionContext.getNonce(transaction.getSender()))
                continue;

            AbstractExecutionContext context = new ChildExecutionContext(this.topExecutionContext);

            context.transfer(transaction.getSender(), transaction.getReceiver(), transaction.getValue());
            context.incrementNonce(transaction.getSender());
            context.commit();

            executed.add(transaction);
        }

        this.topExecutionContext.commit();

        return executed;
    }
}
