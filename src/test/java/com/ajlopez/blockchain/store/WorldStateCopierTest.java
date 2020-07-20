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
        KeyValueStores sourceKeyValueStores = new MemoryKeyValueStores();
        Stores sourceStores = new Stores(sourceKeyValueStores);
        AccountStore accountStore = sourceStores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);

        Address address = FactoryHelper.createRandomAddress();
        Account account = new Account(Coin.TEN, 42, 0, null, null);

        accountStore.putAccount(address, account);
        accountStore.save();

        Hash rootHash = accountStore.getRootHash();

        KeyValueStores targetKeyValueStores = new MemoryKeyValueStores();
        Stores targetStores = new Stores(targetKeyValueStores);

        WorldStateCopier worldStateCopier = new WorldStateCopier(sourceStores, targetStores, rootHash);

        worldStateCopier.process();

        TrieStore targetAccountTrieStore = targetStores.getAccountTrieStore();
        Assert.assertTrue(targetAccountTrieStore.exists(rootHash));

        AccountStore targetAccountStore = targetStores.getAccountStoreProvider().retrieve(rootHash);

        Account result = targetAccountStore.getAccount(address);

        Assert.assertNotNull(result);

        Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));
    }
}
