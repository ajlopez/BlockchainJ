package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.vms.eth.ChildMapStorage;
import com.ajlopez.blockchain.vms.eth.Storage;
import com.ajlopez.blockchain.vms.eth.TrieStorage;
import com.ajlopez.blockchain.vms.eth.TrieStorageProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class ChildExecutionContextTest {
    @Test
    public void getZeroBalanceFromNewAccount() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        Coin result = executionContext.getBalance(new Address(new byte[] { 0x01, 0x02 }));
        
        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ZERO, result);
    }

    @Test
    public void getBalanceFromAccountAndCommitDoesNotChangeStore() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Account account = new Account(Coin.TEN, 42, 0, null, null);
        Address address = FactoryHelper.createRandomAddress();

        accountStore.putAccount(address, account);

        Hash originalHash = accountStore.getRootHash();

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        Coin result = executionContext.getBalance(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result);

        executionContext.commit();

        Coin result2 = accountStore.getAccount(address).getBalance();

        Assert.assertNotNull(result2);
        Assert.assertEquals(Coin.TEN, result2);

        Assert.assertEquals(originalHash, accountStore.getRootHash());
    }

    @Test
    public void getZeroBalanceFromAccountAndCommitDoesNotChangeStore() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Hash originalHash = accountStore.getRootHash();

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        Coin result = executionContext.getBalance(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ZERO, result);

        executionContext.commit();

        Coin result2 = accountStore.getAccount(address).getBalance();

        Assert.assertNotNull(result2);
        Assert.assertEquals(Coin.ZERO, result2);

        Assert.assertEquals(originalHash, accountStore.getRootHash());
    }

    @Test
    public void incrementAccountNonce() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Account account = new Account(Coin.fromUnsignedLong(1000), 41, 0, null, null);
        accountStore.putAccount(address, account);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        executionContext.incrementNonce(address);

        long nonce = executionContext.getNonce(address);
        Assert.assertEquals(42, nonce);

        long originalNonce = accountStore.getAccount(address).getNonce();
        Assert.assertEquals(41, originalNonce);
    }

    @Test
    public void incrementAccountNonceAndCommitOneLevel() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Account account = new Account(Coin.fromUnsignedLong(1000), 41, 0, null, null);
        accountStore.putAccount(address, account);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        executionContext.incrementNonce(address);
        executionContext.commit();

        long nonce = executionContext.getNonce(address);
        Assert.assertEquals(42, nonce);

        long updatedNonce = parentExecutionContext.getNonce(address);
        Assert.assertEquals(42, updatedNonce);

        long originalNonce = accountStore.getAccount(address).getNonce();
        Assert.assertEquals(41, originalNonce);
    }

    @Test
    public void incrementAccountNonceAndCommitTwoLevels() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Account account = new Account(Coin.fromUnsignedLong(1000), 41, 0, null, null);
        accountStore.putAccount(address, account);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        executionContext.incrementNonce(address);
        executionContext.commit();
        parentExecutionContext.commit();

        long nonce = executionContext.getNonce(address);
        Assert.assertEquals(42, nonce);

        long updatedNonce = accountStore.getAccount(address).getNonce();
        Assert.assertEquals(42, updatedNonce);
    }

    @Test
    public void incrementNonceAccountAndRollback() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Account account = new Account(Coin.fromUnsignedLong(1000), 41, 0, null, null);
        accountStore.putAccount(address, account);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        executionContext.incrementNonce(address);
        executionContext.rollback();

        long nonce = executionContext.getNonce(address);
        Assert.assertEquals(41, nonce);

        long parentNonce = parentExecutionContext.getNonce(address);
        Assert.assertEquals(41, parentNonce);

        long originalNonce = accountStore.getAccount(address).getNonce();
        Assert.assertEquals(41, originalNonce);
    }

    @Test
    public void getNonceFromNewAccountAndCommit() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Hash originalHash = accountStore.getRootHash();

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        long nonce = executionContext.getNonce(address);
        Assert.assertEquals(0, nonce);

        executionContext.commit();

        long originalNonce = accountStore.getAccount(address).getNonce();
        Assert.assertEquals(0, originalNonce);

        Assert.assertEquals(originalHash, accountStore.getRootHash());
    }

    @Test
    public void transferToAccount() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(Coin.fromUnsignedLong(1000), 42, 0, null, null);
        accountStore.putAccount(senderAddress, sender);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        executionContext.transfer(senderAddress, receiverAddress, Coin.fromUnsignedLong(100));

        Coin senderBalance = executionContext.getBalance(senderAddress);
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(1000 - 100), senderBalance);

        Coin receiverBalance = executionContext.getBalance(receiverAddress);
        Assert.assertNotNull(receiverBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance);

        Account sender2 = accountStore.getAccount(senderAddress);
        Assert.assertNotNull(sender2);
        Assert.assertEquals(Coin.fromUnsignedLong(1000), sender2.getBalance());

        Account receiver2 = accountStore.getAccount(receiverAddress);
        Assert.assertNotNull(receiver2);
        Assert.assertEquals(Coin.ZERO, receiver2.getBalance());
    }

    @Test
    public void transferToAccountAndCommitOneLevel() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(Coin.fromUnsignedLong(1000), 42, 0, null, null);
        accountStore.putAccount(senderAddress, sender);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        executionContext.transfer(senderAddress, receiverAddress, Coin.fromUnsignedLong(100));
        executionContext.commit();

        Coin senderBalance = executionContext.getBalance(senderAddress);
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(1000 - 100), senderBalance);

        Coin receiverBalance = executionContext.getBalance(receiverAddress);
        Assert.assertNotNull(receiverBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance);

        Coin senderBalance2 = parentExecutionContext.getBalance(senderAddress);
        Assert.assertNotNull(senderBalance2);
        Assert.assertEquals(Coin.fromUnsignedLong(1000 - 100), senderBalance2);

        Coin receiverBalance2 = parentExecutionContext.getBalance(receiverAddress);
        Assert.assertNotNull(receiverBalance2);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance2);

        Account sender2 = accountStore.getAccount(senderAddress);
        Assert.assertNotNull(sender2);
        Assert.assertEquals(Coin.fromUnsignedLong(1000), sender2.getBalance());

        Account receiver2 = accountStore.getAccount(receiverAddress);
        Assert.assertNotNull(receiver2);
        Assert.assertEquals(Coin.ZERO, receiver2.getBalance());
    }

    @Test
    public void transferToAccountAndCommitTwoLevels() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(Coin.fromUnsignedLong(1000), 42, 0, null, null);
        accountStore.putAccount(senderAddress, sender);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        executionContext.transfer(senderAddress, receiverAddress, Coin.fromUnsignedLong(100));
        executionContext.commit();
        parentExecutionContext.commit();

        Coin senderBalance = executionContext.getBalance(senderAddress);
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(1000 - 100), senderBalance);

        Coin receiverBalance = executionContext.getBalance(receiverAddress);
        Assert.assertNotNull(receiverBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance);

        Account sender2 = accountStore.getAccount(senderAddress);
        Assert.assertNotNull(sender2);
        Assert.assertEquals(Coin.fromUnsignedLong(1000 - 100), sender2.getBalance());

        Account receiver2 = accountStore.getAccount(receiverAddress);
        Assert.assertNotNull(receiver2);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiver2.getBalance());
    }

    @Test
    public void transferToAccountAndRollback() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(Coin.fromUnsignedLong(1000), 42, 0, null, null);
        accountStore.putAccount(senderAddress, sender);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        executionContext.transfer(senderAddress, receiverAddress, Coin.fromUnsignedLong(100));
        executionContext.rollback();

        Coin senderBalance = executionContext.getBalance(senderAddress);
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(1000), senderBalance);

        Coin receiverBalance = executionContext.getBalance(receiverAddress);
        Assert.assertNotNull(receiverBalance);
        Assert.assertEquals(Coin.ZERO, receiverBalance);

        Account sender2 = accountStore.getAccount(senderAddress);
        Assert.assertNotNull(sender2);
        Assert.assertEquals(Coin.fromUnsignedLong(1000), sender2.getBalance());

        Account receiver2 = accountStore.getAccount(receiverAddress);
        Assert.assertNotNull(receiver2);
        Assert.assertEquals(Coin.ZERO, receiver2.getBalance());
    }

    @Test
    public void getNullCodeHashFromNewAccount() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        Hash result = executionContext.getCodeHash(new Address(new byte[] { 0x01, 0x02 }));

        Assert.assertNull(result);
    }

    @Test
    public void getNullStorageHashFromNewAccount() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        Hash result = executionContext.getAccountState(new Address(new byte[] { 0x01, 0x02 })).getStorageHash();

        Assert.assertNull(result);
    }

    @Test
    public void getStorageFromNewAccountAndSetKeyValue() throws IOException {
        Address address = FactoryHelper.createRandomAddress();
        AccountStore accountStore = new AccountStore(new Trie());
        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);
        TrieStorageProvider trieStorageProvider = new TrieStorageProvider(trieStore);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, trieStorageProvider, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        Storage result = executionContext.getAccountStorage(address);

        DataWord key = FactoryHelper.createRandomDataWord();
        DataWord value = FactoryHelper.createRandomDataWord();

        result.setValue(key, value);

        Assert.assertEquals(value, result.getValue(key));
        Assert.assertEquals(DataWord.ZERO, parentExecutionContext.getAccountStorage(address).getValue(key));

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ChildMapStorage);

        Assert.assertNull(executionContext.getAccountState(address).getStorageHash());

        executionContext.commit();

        Storage result2 = parentExecutionContext.getAccountStorage(address);

        Assert.assertNotNull(result2);
        Assert.assertTrue(result2 instanceof TrieStorage);

        TrieStorage tresult2 = (TrieStorage)result2;

        Assert.assertNotEquals(Trie.EMPTY_TRIE_HASH, tresult2.getRootHash());

        Assert.assertEquals(value, tresult2.getValue(key));

        Assert.assertNull(parentExecutionContext.getAccountState(address).getStorageHash());
        Assert.assertNull(keyValueStore.getValue(tresult2.getRootHash().getBytes()));

        parentExecutionContext.commit();

        Assert.assertNotNull(parentExecutionContext.getAccountState(address).getStorageHash());
        Assert.assertEquals(tresult2.getRootHash(), parentExecutionContext.getAccountState(address).getStorageHash());
        Assert.assertNotNull(keyValueStore.getValue(tresult2.getRootHash().getBytes()));
    }

    @Test
    public void getStorageFromNewAccountAndSetKeyValueAndRollback() throws IOException {
        Address address = FactoryHelper.createRandomAddress();
        AccountStore accountStore = new AccountStore(new Trie());
        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);
        TrieStorageProvider trieStorageProvider = new TrieStorageProvider(trieStore);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, trieStorageProvider, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        Storage result = executionContext.getAccountStorage(address);

        DataWord key = FactoryHelper.createRandomDataWord();
        DataWord value = FactoryHelper.createRandomDataWord();

        result.setValue(key, value);

        Assert.assertEquals(value, result.getValue(key));
        Assert.assertEquals(DataWord.ZERO, parentExecutionContext.getAccountStorage(address).getValue(key));

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ChildMapStorage);

        Assert.assertNull(executionContext.getAccountState(address).getStorageHash());

        executionContext.rollback();

        Assert.assertNull(executionContext.getAccountState(address).getStorageHash());

        Storage result2 = parentExecutionContext.getAccountStorage(address);

        Assert.assertNotNull(result2);
        Assert.assertTrue(result2 instanceof TrieStorage);

        TrieStorage tresult2 = (TrieStorage)result2;

        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, tresult2.getRootHash());

        Assert.assertNull(parentExecutionContext.getAccountState(address).getStorageHash());
        Assert.assertNull(keyValueStore.getValue(tresult2.getRootHash().getBytes()));
    }

    @Test
    public void setCode() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();
        byte[] code = FactoryHelper.createRandomBytes(42);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        Assert.assertNull(executionContext.getCode(address));

        executionContext.setCode(address, code);

        Assert.assertNotNull(executionContext.getCodeHash(address));
        Assert.assertArrayEquals(code, executionContext.getCode(address));
        Assert.assertEquals(code.length, executionContext.getAccountState(address).getCodeLength());
    }

    @Test
    public void setCodeAndCommit() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        Address address = FactoryHelper.createRandomAddress();
        byte[] code = FactoryHelper.createRandomBytes(42);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        Assert.assertNull(executionContext.getCode(address));

        executionContext.setCode(address, code);

        Assert.assertNotNull(executionContext.getCodeHash(address));
        Assert.assertArrayEquals(code, executionContext.getCode(address));

        executionContext.commit();

        Assert.assertNotNull(executionContext.getCodeHash(address));
        Assert.assertArrayEquals(code, executionContext.getCode(address));

        Assert.assertNotNull(parentExecutionContext.getCodeHash(address));
        Assert.assertArrayEquals(code, parentExecutionContext.getCode(address));
    }

    @Test
    public void setCodeAndRollback() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        Address address = FactoryHelper.createRandomAddress();
        byte[] code = FactoryHelper.createRandomBytes(42);

        TopExecutionContext parentExecutionContext = new TopExecutionContext(accountStore, null, null);
        ChildExecutionContext executionContext = new ChildExecutionContext(parentExecutionContext);

        Assert.assertNull(executionContext.getCode(address));

        executionContext.setCode(address, code);

        Assert.assertNotNull(executionContext.getCodeHash(address));
        Assert.assertArrayEquals(code, executionContext.getCode(address));

        executionContext.rollback();

        Assert.assertNull(executionContext.getCodeHash(address));
        Assert.assertNull(parentExecutionContext.getCodeHash(address));
    }
}
