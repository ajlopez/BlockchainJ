package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.state.Trie;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class AccountStore {
    private Trie trie;

    public AccountStore(Trie trie) {
        this.trie = trie;
    }

    public Account getAccount(Address address) {
        return new Account();
    }
}
