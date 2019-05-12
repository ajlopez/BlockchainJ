package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 12/05/2019.
 */
public class World {
    private final AccountStore accountStore;
    private final Map<String, Address> accounts;

    public World() {
        this.accountStore = new AccountStore(new Trie());
        this.accounts = new HashMap<>();
    }

    public Account getAccount(String name) {
        if (!this.accounts.containsKey(name))
            return null;

        return this.accountStore.getAccount(this.accounts.get(name));
    }

    public void setAccount(String name, Account account) {
        Address address = FactoryHelper.createRandomAddress();
        this.accounts.put(name, address);
        this.accountStore.putAccount(address, account);
    }
}
