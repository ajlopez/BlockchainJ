package com.ajlopez.blockchain.test.builders;

import com.ajlopez.blockchain.execution.BlockExecutor;
import com.ajlopez.blockchain.execution.ExecutionContext;
import com.ajlopez.blockchain.execution.TopExecutionContext;
import com.ajlopez.blockchain.execution.TransactionExecutor;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.vms.eth.TrieStorageProvider;

import java.io.IOException;

/**
 * Created by ajlopez on 17/12/2019.
 */
public class ExecutorBuilder {
    private TrieStore accountTrieStore;
    private AccountStoreProvider accountStoreProvider;
    private AccountStore accountStore;
    private TrieStorageProvider trieStorageProvider;
    private CodeStore codeStore;

    public TrieStore getAccountTrieStore() {
        if (this.accountTrieStore == null)
            this.accountTrieStore = new TrieStore(new HashMapStore());

        return this.accountTrieStore;
    }

    public AccountStoreProvider getAccountStoreProvider() {
        if (this.accountStoreProvider == null)
            this.accountStoreProvider = new AccountStoreProvider(this.getAccountTrieStore());

        return this.accountStoreProvider;
    }

    public AccountStore getAccountStore() throws IOException {
        if (this.accountStore == null)
            this.accountStore = getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);

        return this.accountStore;
    }

    public TrieStorageProvider getTrieStorageProvider() {
        if (this.trieStorageProvider == null)
            this.trieStorageProvider = new TrieStorageProvider(new TrieStore(new HashMapStore()));

        return this.trieStorageProvider;
    }

    public CodeStore getCodeStore() {
        if (this.codeStore == null)
            this.codeStore = new CodeStore(new HashMapStore());

        return this.codeStore;
    }

    public ExecutionContext buildExecutionContext() throws IOException {
        return new TopExecutionContext(
            this.getAccountStore(),
            this.getTrieStorageProvider(),
            this.getCodeStore()
        );
    }

    public TransactionExecutor buildTransactionExecutor() throws IOException {
        return new TransactionExecutor(this.buildExecutionContext());
    }

    public BlockExecutor buildBlockExecutor() {
        return new BlockExecutor(
            this.getAccountStoreProvider(),
            this.getTrieStorageProvider(),
            this.getCodeStore()
        );
    }
}
