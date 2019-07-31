package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.CodeStore;

/**
 * Created by ajlopez on 30/05/2019.
 */
public class BlockExecutor {
    private final AccountStoreProvider accountStoreProvider;
    private final CodeStore codeStore;

    public BlockExecutor(AccountStoreProvider accountStoreProvider, CodeStore codeStore) {
        this.accountStoreProvider = accountStoreProvider;
        this.codeStore = codeStore;
    }

    public Hash executeBlock(Block block, Hash initialStateRoot) {
        AccountStore accountStore = this.accountStoreProvider.retrieve(initialStateRoot);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, this.codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(block.getTransactions());

        return accountStore.getRootHash();
    }
}
