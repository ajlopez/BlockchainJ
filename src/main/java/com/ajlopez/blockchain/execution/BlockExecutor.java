package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.CodeStore;
import com.ajlopez.blockchain.vms.eth.BlockData;

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

        // TODO get difficulty from block
        BlockData blockData = new BlockData(block.getNumber(), block.getTimestamp(), block.getCoinbase(), DataWord.ONE);
        transactionExecutor.executeTransactions(block.getTransactions(), null);

        return accountStore.getRootHash();
    }
}
