package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
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

    @Test
    public void transferToAccount() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(BigInteger.valueOf(1000), 42);
        accountStore.putAccount(senderAddress, sender);

        ExecutionContext executionContext = new ExecutionContext(accountStore);

        executionContext.transfer(senderAddress, receiverAddress, BigInteger.valueOf(100));

        BigInteger senderBalance = executionContext.getBalance(senderAddress);
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(BigInteger.valueOf(1000 - 100), senderBalance);

        BigInteger receiverBalance = executionContext.getBalance(receiverAddress);
        Assert.assertNotNull(receiverBalance);
        Assert.assertEquals(BigInteger.valueOf(100), receiverBalance);

        Account sender2 = accountStore.getAccount(senderAddress);
        Assert.assertNotNull(sender2);
        Assert.assertEquals(BigInteger.valueOf(1000), sender2.getBalance());

        Account receiver2 = accountStore.getAccount(receiverAddress);
        Assert.assertNotNull(receiver2);
        Assert.assertEquals(BigInteger.ZERO, receiver2.getBalance());
    }

    @Test
    public void transferToAccountAndCommit() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(BigInteger.valueOf(1000), 42);
        accountStore.putAccount(senderAddress, sender);

        ExecutionContext executionContext = new ExecutionContext(accountStore);

        executionContext.transfer(senderAddress, receiverAddress, BigInteger.valueOf(100));
        executionContext.commit();

        BigInteger senderBalance = executionContext.getBalance(senderAddress);
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(BigInteger.valueOf(1000 - 100), senderBalance);

        BigInteger receiverBalance = executionContext.getBalance(receiverAddress);
        Assert.assertNotNull(receiverBalance);
        Assert.assertEquals(BigInteger.valueOf(100), receiverBalance);

        Account sender2 = accountStore.getAccount(senderAddress);
        Assert.assertNotNull(sender2);
        Assert.assertEquals(BigInteger.valueOf(1000 - 100), sender2.getBalance());

        Account receiver2 = accountStore.getAccount(receiverAddress);
        Assert.assertNotNull(receiver2);
        Assert.assertEquals(BigInteger.valueOf(100), receiver2.getBalance());
    }

    @Test
    public void transferToAccountAndRollback() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(BigInteger.valueOf(1000), 42);
        accountStore.putAccount(senderAddress, sender);

        ExecutionContext executionContext = new ExecutionContext(accountStore);

        executionContext.transfer(senderAddress, receiverAddress, BigInteger.valueOf(100));
        executionContext.rollback();

        BigInteger senderBalance = executionContext.getBalance(senderAddress);
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(BigInteger.valueOf(1000), senderBalance);

        BigInteger receiverBalance = executionContext.getBalance(receiverAddress);
        Assert.assertNotNull(receiverBalance);
        Assert.assertEquals(BigInteger.ZERO, receiverBalance);

        Account sender2 = accountStore.getAccount(senderAddress);
        Assert.assertNotNull(sender2);
        Assert.assertEquals(BigInteger.valueOf(1000), sender2.getBalance());

        Account receiver2 = accountStore.getAccount(receiverAddress);
        Assert.assertNotNull(receiver2);
        Assert.assertEquals(BigInteger.ZERO, receiver2.getBalance());
    }
}
