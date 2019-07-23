package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.execution.AccountState;
import com.ajlopez.blockchain.store.TrieStore;

/**
 * Created by ajlopez on 23/07/2019.
 */
public class TrieStorageProvider {
    private final TrieStore storageTrieStore;

    public TrieStorageProvider(TrieStore storageTrieStore) {
        this.storageTrieStore = storageTrieStore;
    }

    public TrieStorage retrieve(AccountState accountState) {
        return new TrieStorage(this.storageTrieStore.retrieve(accountState.getStorageHash()), accountState);
    }
}
