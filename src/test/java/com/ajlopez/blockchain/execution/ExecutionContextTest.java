package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class ExecutionContextTest {
    @Test
    public void getZeroBalanceFromNewAccount() {
        AccountStore accountStore = new AccountStore(new Trie());

        ExecutionContext executionContext = new ExecutionContext(accountStore);

        BigInteger result = executionContext.getBalance(new Address(new byte[] { 0x01, 0x02 }));
        
        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result);
    }
}
