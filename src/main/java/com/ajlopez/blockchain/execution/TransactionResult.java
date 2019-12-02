package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.vms.eth.ExecutionResult;

public class TransactionResult {
    private final Transaction transaction;
    private final ExecutionResult executionResult;

    public TransactionResult(Transaction transaction, ExecutionResult executionResult) {
        this.transaction = transaction;
        this.executionResult = executionResult;
    }

    public Transaction getTransaction() { return this.transaction; }

    public ExecutionResult getExecutionResult() { return this.executionResult; }
}
