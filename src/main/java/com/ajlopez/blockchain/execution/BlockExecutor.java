package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.CodeStore;
import com.ajlopez.blockchain.vms.eth.BlockData;
import com.ajlopez.blockchain.vms.eth.TrieStorageProvider;

import java.io.IOException;

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
        transactionExecutor.executeTransactions(block.getTransactions(), blockData);

        return new BlockExecutionResult(accountStore.getRootHash(), null);
    }
}
