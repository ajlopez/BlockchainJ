package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 01/12/2018.
 */
public class AccountStoreProviderTest {
    @Test
    public void retrieveEmptyAccountStore() {
        AccountStoreProvider provider = new AccountStoreProvider(new TrieStore(new HashMapStore()));

        AccountStore result = provider.retrieve(Trie.EMPTY_TRIE_HASH);

        Assert.assertNotNull(result);

        Account account = result.getAccount(FactoryHelper.createRandomAddress());

        Assert.assertNotNull(account);
        Assert.assertEquals(BigInteger.ZERO, account.getBalance());
        Assert.assertEquals(0, account.getNonce());
    }

    @Test
    public void retrieveAccountStores() {
        Address address = FactoryHelper.createRandomAddress();

        TrieStore accountTrieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider provider = new AccountStoreProvider(accountTrieStore);

        AccountStore accountStore = provider.retrieve(Trie.EMPTY_TRIE_HASH);

        Hash hash0 = accountStore.getRootHash();
        accountStore.save();

        Account account1 = new Account(BigInteger.ONE, 3, null);
        accountStore.putAccount(address, account1);
        Hash hash1 = accountStore.getRootHash();
        accountStore.save();

        Account account2 = new Account(BigInteger.TEN, 42, null);

        accountStore.putAccount(address, account2);
        Hash hash2 = accountStore.getRootHash();
        accountStore.save();

        Assert.assertNotEquals(hash0, hash1);
        Assert.assertNotEquals(hash0, hash2);
        Assert.assertNotEquals(hash1, hash2);

        AccountStore result0 = provider.retrieve(hash0);

        Assert.assertNotNull(result0);

        Account account0 = result0.getAccount(address);

        Assert.assertNotNull(account0);
        Assert.assertEquals(BigInteger.ZERO, account0.getBalance());
        Assert.assertEquals(0, account0.getNonce());

        AccountStore result1 = provider.retrieve(hash1);

        Assert.assertNotNull(result1);

        Account account1b = result1.getAccount(address);

        Assert.assertNotNull(account1b);
        Assert.assertEquals(BigInteger.ONE, account1b.getBalance());
        Assert.assertEquals(3, account1b.getNonce());

        AccountStore result2 = provider.retrieve(hash2);

        Assert.assertNotNull(result2);

        Account account2b = result2.getAccount(address);

        Assert.assertNotNull(account2b);
        Assert.assertEquals(BigInteger.TEN, account2b.getBalance());
        Assert.assertEquals(42, account2b.getNonce());
    }
}
