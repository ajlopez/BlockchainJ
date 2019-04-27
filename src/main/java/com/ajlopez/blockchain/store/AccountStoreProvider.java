package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.types.Hash;

/**
 * Created by ajlopez on 01/12/2018.
 */
public class AccountStoreProvider {
    private final TrieStore accountTrieStore;

    public AccountStoreProvider(TrieStore accountTrieStore) {
        this.accountTrieStore = accountTrieStore;
    }

    public AccountStore retrieve(Hash hash) {
        return new AccountStore(this.accountTrieStore.retrieve(hash));
    }
}
