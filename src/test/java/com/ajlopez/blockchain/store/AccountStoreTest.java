package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class AccountStoreTest {
    @Test
    public void getUnknownAccount() {
        AccountStore store = new AccountStore(null);
        Address address = new Address(new byte[] { 0x01, 0x02 });

        Account result = store.getAccount(address);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }
}
