package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
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
}
