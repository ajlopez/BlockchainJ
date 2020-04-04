package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class AccountStoreTest {
    @Test
    public void getUnknownAccount() throws IOException {
        AccountStore store = new AccountStore(new Trie());
        Address address = new Address(new byte[] { 0x01, 0x02 });

        Account result = store.getAccount(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void putAndGetAccount() throws IOException {
        AccountStore store = new AccountStore(new Trie());
        Account account = new Account(Coin.TEN, 42, null, null);
        Address address = new Address(new byte[] { 0x01, 0x02 });

        store.putAccount(address, account);

        Account result = store.getAccount(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());

        Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));
    }

    // TODO move to main performance entry point
    @Test
    @Ignore
    public void createTenMillionAccounts() throws IOException {
        int naccounts = 10000000;

        Trie trie = new Trie();
        AccountStore store = new AccountStore(trie);

        System.out.println("KB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
        long millis = System.currentTimeMillis();

        for (int k = 0; k < naccounts; k++) {
            Address address = FactoryHelper.createRandomAddress();
            Account account = new Account(Coin.TEN, 0, null, null);

            store.putAccount(address, account);
        }

        millis = System.currentTimeMillis() - millis;
        System.out.println(millis);
        System.out.println("KB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
        System.out.println("Trie size: " + store.getTrie().nodesSize());
    }
}
