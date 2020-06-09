package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.core.types.Hash;

import java.util.List;

/**
 * Created by ajlopez on 09/06/2020.
 */
public class BlockExecutionResult {
    private final Hash stateRootHash;
    private final List<TransactionReceipt> transactionReceipts;

    public BlockExecutionResult(Hash stateRootHash, List<TransactionReceipt> transactionReceipts) {
        this.stateRootHash = stateRootHash;
        this.transactionReceipts = transactionReceipts;
    }

    public Hash getStateRootHash() { return this.stateRootHash; }

    public List<TransactionReceipt> getTransactionReceipts() {
        return this.transactionReceipts;
    }
}

