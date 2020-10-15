package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import com.ajlopez.blockchain.state.Trie;

import java.io.IOException;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class AccountStore {
    private Trie trie;

    public AccountStore(Trie trie) {
        this.trie = trie;
    }

    public Account getAccount(Address address) throws IOException {
        byte[] key = address.getBytes();
        byte[] value = this.trie.get(key);

        if (value == null)
            return new Account();

        return AccountEncoder.decode(value);
    }

    public void putAccount(Address address, Account account) throws IOException {
        if (account.isEmpty())
            throw new IllegalArgumentException("Empty account");

        byte[] key = address.getBytes();
        byte[] value = AccountEncoder.encode(account);

        this.trie = this.trie.put(key, value);
    }

    public Hash getRootHash() {
        return this.trie.getHash();
    }

    public void save() throws IOException {
        this.trie.save();
    }
}
