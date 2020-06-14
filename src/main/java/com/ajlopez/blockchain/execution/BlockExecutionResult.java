package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.merkle.MerkleTreeBuilder;

import java.util.List;

/**
 * Created by ajlopez on 09/06/2020.
 */
public class BlockExecutionResult {
    private final Hash stateRootHash;
    private final List<TransactionReceipt> transactionReceipts;

    private Hash transactionReceiptsHash;

    public BlockExecutionResult(Hash stateRootHash, List<TransactionReceipt> transactionReceipts) {
        this.stateRootHash = stateRootHash;
        this.transactionReceipts = transactionReceipts;
    }

    public Hash getStateRootHash() { return this.stateRootHash; }

    public List<TransactionReceipt> getTransactionReceipts() {
        return this.transactionReceipts;
    }

    public Hash getTransactionReceiptsHash() {
        if (this.transactionReceiptsHash != null)
            return this.transactionReceiptsHash;

        this.transactionReceiptsHash = calculateTransactionReceiptsHash(this.transactionReceipts);

        return this.transactionReceiptsHash;
    }

    public static Hash calculateTransactionReceiptsHash(List<TransactionReceipt> transactionReceipts) {
        MerkleTreeBuilder merkleTreeBuilder = new MerkleTreeBuilder();

        if (transactionReceipts == null)
            return merkleTreeBuilder.build().getHash();

        for (TransactionReceipt transactionReceipt : transactionReceipts)
            merkleTreeBuilder.add(transactionReceipt.getHash());

        return merkleTreeBuilder.build().getHash();
    }
}

