package com.ajlopez.blockchain.test.builders;

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
    private AccountStoreProvider accountStoreProvider;
    private AccountStore accountStore;
    private TrieStorageProvider trieStorageProvider;
    private CodeStore codeStore;

    public AccountStoreProvider getAccountStoreProvider() {
        return this.accountStoreProvider;
    }

    public AccountStore getAccountStore() {
        return this.accountStore;
    }

    public TrieStorageProvider getTrieStorageProvider() {
        return this.trieStorageProvider;
    }

    public CodeStore getCodeStore() {
        return this.codeStore;
    }

    public ExecutionContext buildExecutionContext() throws IOException {
        if (this.accountStore == null)
            this.accountStore = new AccountStore(new Trie());

        if (this.trieStorageProvider == null)
            this.trieStorageProvider = new TrieStorageProvider(new TrieStore(new HashMapStore()));

        if (this.codeStore == null)
            this.codeStore = new CodeStore(new HashMapStore());

        return new TopExecutionContext(
            this.accountStore,
            this.trieStorageProvider,
            this.codeStore
        );
    }

    public TransactionExecutor buildTransactionExecutor() throws IOException {
        return new TransactionExecutor(this.buildExecutionContext());
    }
}
