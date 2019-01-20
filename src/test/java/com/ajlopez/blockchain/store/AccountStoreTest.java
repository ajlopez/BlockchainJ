package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import com.ajlopez.blockchain.state.Trie;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class AccountStoreTest {
    @Test
    public void getUnknownAccount() {
        AccountStore store = new AccountStore(new Trie());
        Address address = new Address(new byte[] { 0x01, 0x02 });

        Account result = store.getAccount(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void putAndGetAccount() {
        AccountStore store = new AccountStore(new Trie());
        Account account = new Account(BigInteger.TEN, 42, null);
        Address address = new Address(new byte[] { 0x01, 0x02 });

        store.putAccount(address, account);

        Account result = store.getAccount(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());

        Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));
    }
}
