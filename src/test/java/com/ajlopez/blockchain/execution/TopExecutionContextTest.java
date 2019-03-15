package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.KeyValueStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.vms.eth.Storage;
import com.ajlopez.blockchain.vms.eth.TrieStorage;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class TopExecutionContextTest {
    @Test
    public void getZeroBalanceFromNewAccount() {
        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

        BigInteger result = executionContext.getBalance(new Address(new byte[] { 0x01, 0x02 }));
        
        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result);
    }

    @Test
    public void getNullCodeHashFromNewAccount() {
        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

        Hash result = executionContext.getCodeHash(new Address(new byte[] { 0x01, 0x02 }));

        Assert.assertNull(result);
    }

    @Test
    public void getNullStorageHashFromNewAccount() {
        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

        Hash result = executionContext.getAccountState(new Address(new byte[] { 0x01, 0x02 })).getStorageHash();

        Assert.assertNull(result);
    }

    @Test
    public void setAndGetCodeHashFromNewAccount() {
        Hash codeHash = FactoryHelper.createRandomHash();
        Address address = FactoryHelper.createRandomAddress();

        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

        executionContext.setCodeHash(address, codeHash);

        Hash result = executionContext.getCodeHash(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(codeHash, result);
    }

    @Test
    public void getEmptyStorageFromNewAccount() {
        AccountStore accountStore = new AccountStore(new Trie());
        TrieStore trieStore = new TrieStore(new HashMapStore());

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, trieStore);

        Storage result = executionContext.getAccountStorage(new Address(new byte[] { 0x01, 0x02 }));

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof TrieStorage);

        TrieStorage tresult = (TrieStorage)result;

        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, tresult.getRootHash());
    }

    @Test
    public void getStorageFromNewAccountAndSetKeyValue() {
        Address address = FactoryHelper.createRandomAddress();
        AccountStore accountStore = new AccountStore(new Trie());
        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, trieStore);

        Storage result = executionContext.getAccountStorage(address);

        DataWord key = FactoryHelper.createRandomDataWord();
        DataWord value = FactoryHelper.createRandomDataWord();

        result.setValue(key, value);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof TrieStorage);

        TrieStorage tresult = (TrieStorage)result;

        Assert.assertNotEquals(Trie.EMPTY_TRIE_HASH, tresult.getRootHash());

        Assert.assertNull(keyValueStore.getValue(tresult.getRootHash().getBytes()));

        Storage result2 = executionContext.getAccountStorage(address);

        Assert.assertNotNull(result2);
        Assert.assertTrue(result2 instanceof TrieStorage);
        Assert.assertEquals(tresult.getRootHash(), ((TrieStorage)result2).getRootHash());

        Assert.assertNull(executionContext.getAccountState(address).getStorageHash());

        executionContext.commit();

        Assert.assertEquals(tresult.getRootHash(), executionContext.getAccountState(address).getStorageHash());

        Account account = accountStore.getAccount(address);

        Assert.assertNotNull(account);
        Assert.assertEquals(tresult.getRootHash(), account.getStorageHash());
    }

    @Test
    public void getBalanceFromAccountAndCommitDoesNotChangeStore() {
        AccountStore accountStore = new AccountStore(new Trie());
        Account account = new Account(BigInteger.TEN, 42, null, null);
        Address address = FactoryHelper.createRandomAddress();

        accountStore.putAccount(address, account);

        Hash originalHash = accountStore.getRootHash();

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

        BigInteger result = executionContext.getBalance(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result);

        executionContext.commit();

        BigInteger result2 = accountStore.getAccount(address).getBalance();

        Assert.assertNotNull(result2);
        Assert.assertEquals(BigInteger.TEN, result2);

        Assert.assertEquals(originalHash, accountStore.getRootHash());
    }

    @Test
    public void getZeroBalanceFromAccountAndCommitDoesNotChangeStore() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Hash originalHash = accountStore.getRootHash();

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

        BigInteger result = executionContext.getBalance(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result);

        executionContext.commit();

        BigInteger result2 = accountStore.getAccount(address).getBalance();

        Assert.assertNotNull(result2);
        Assert.assertEquals(BigInteger.ZERO, result2);

        Assert.assertEquals(originalHash, accountStore.getRootHash());
    }

    @Test
    public void incrementNonceAccount() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Account account = new Account(BigInteger.valueOf(1000), 41, null, null);
        accountStore.putAccount(address, account);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

        executionContext.incrementNonce(address);

        long nonce = executionContext.getNonce(address);
        Assert.assertEquals(42, nonce);

        long originalNonce = accountStore.getAccount(address).getNonce();
        Assert.assertEquals(41, originalNonce);
    }

    @Test
    public void incrementNonceAccountAndCommit() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Account account = new Account(BigInteger.valueOf(1000), 41, null, null);
        accountStore.putAccount(address, account);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

        executionContext.incrementNonce(address);
        executionContext.commit();

        long nonce = executionContext.getNonce(address);
        Assert.assertEquals(42, nonce);

        long updatedNonce = accountStore.getAccount(address).getNonce();
        Assert.assertEquals(42, updatedNonce);
    }

    @Test
    public void incrementNonceAccountAndRollback() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Account account = new Account(BigInteger.valueOf(1000), 41, null, null);
        accountStore.putAccount(address, account);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

        executionContext.incrementNonce(address);
        executionContext.rollback();

        long nonce = executionContext.getNonce(address);
        Assert.assertEquals(41, nonce);

        long updatedNonce = accountStore.getAccount(address).getNonce();
        Assert.assertEquals(41, updatedNonce);
    }

    @Test
    public void getNonceFromNewAccountAndCommit() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Hash originalHash = accountStore.getRootHash();

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

        long nonce = executionContext.getNonce(address);
        Assert.assertEquals(0, nonce);

        executionContext.commit();

        long originalNonce = accountStore.getAccount(address).getNonce();
        Assert.assertEquals(0, originalNonce);

        Assert.assertEquals(originalHash, accountStore.getRootHash());
    }

    @Test
    public void transferToAccount() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(BigInteger.valueOf(1000), 42, null, null);
        accountStore.putAccount(senderAddress, sender);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

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

        Account sender = new Account(BigInteger.valueOf(1000), 42, null, null);
        accountStore.putAccount(senderAddress, sender);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

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

        Account sender = new Account(BigInteger.valueOf(1000), 42, null, null);
        accountStore.putAccount(senderAddress, sender);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null);

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
