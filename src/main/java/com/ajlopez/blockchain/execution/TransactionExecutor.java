package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class TransactionExecutor {
    private final ExecutionContext executionContext;

    public TransactionExecutor(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public List<Transaction> executeTransactions(List<Transaction> transactions) {
        List<Transaction> executed = new ArrayList<>();

        for (Transaction transaction : transactions)
            if (this.executeTransaction(transaction))
                executed.add(transaction);

        this.executionContext.commit();

        return executed;
    }

    private boolean executeTransaction(Transaction transaction) {
        Address sender = transaction.getSender();

        if (transaction.getNonce() != this.executionContext.getNonce(sender))
            return false;

        BigInteger senderBalance = this.executionContext.getBalance(sender);

        if (senderBalance.compareTo(transaction.getValue()) < 0)
            return false;

        ExecutionContext context = new ChildExecutionContext(this.executionContext);

        context.transfer(transaction.getSender(), transaction.getReceiver(), transaction.getValue());
        context.incrementNonce(transaction.getSender());
        context.commit();

        return true;
    }
}
