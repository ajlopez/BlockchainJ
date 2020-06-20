package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.CodeStore;
import com.ajlopez.blockchain.vms.eth.BlockData;
import com.ajlopez.blockchain.vms.eth.ExecutionResult;
import com.ajlopez.blockchain.vms.eth.TrieStorageProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 30/05/2019.
 */
public class BlockExecutor {
    private final AccountStoreProvider accountStoreProvider;
    private final TrieStorageProvider trieStorageProvider;
    private final CodeStore codeStore;

    public BlockExecutor(AccountStoreProvider accountStoreProvider, TrieStorageProvider trieStorageProvider, CodeStore codeStore) {
        this.accountStoreProvider = accountStoreProvider;
        this.trieStorageProvider = trieStorageProvider;
        this.codeStore = codeStore;
    }

    public BlockExecutionResult executeBlock(Block block, Hash initialStateRoot) throws IOException {
        AccountStore accountStore = this.accountStoreProvider.retrieve(initialStateRoot);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, this.trieStorageProvider, this.codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        BlockData blockData = new BlockData(block.getNumber(), block.getTimestamp(), block.getCoinbase(), block.getDifficulty());
        List<TransactionResult> transactionResults = transactionExecutor.executeTransactions(block.getTransactions(), blockData);

        List<TransactionReceipt> transactionReceipts = new ArrayList<>(transactionResults.size());

        for (TransactionResult transactionResult : transactionResults) {
            ExecutionResult executionResult = transactionResult.getExecutionResult();

            if (executionResult == null)
                continue;

            transactionReceipts.add(executionResult.toTransactionReceipt());
        }

        return new BlockExecutionResult(accountStore.getRootHash(), transactionReceipts);
    }
}
