package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 19/07/2020.
 */
public class WorldStateCopierTest {
    @Test
    public void copyOneAccount() throws IOException {
        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Address address = FactoryHelper.createRandomAddress();
        Account account = new Account(Coin.TEN, 42, 0, null, null);

        accountStore.putAccount(address, account);
        accountStore.save();

        Hash rootHash = accountStore.getRootHash();

        KeyValueStore targetKeyValueStore = new HashMapStore();
        TrieStore targetTrieStore = new TrieStore(targetKeyValueStore);

        WorldStateCopier worldStateCopier = new WorldStateCopier(trieStore, targetTrieStore, rootHash);

        worldStateCopier.process();

        Assert.assertTrue(targetTrieStore.exists(rootHash));

        AccountStore targetAccountStore = new AccountStore(targetTrieStore.retrieve(rootHash));

        Account result = targetAccountStore.getAccount(address);

        Assert.assertNotNull(result);

        Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));
    }
}
