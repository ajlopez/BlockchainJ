package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.vms.eth.Storage;
import com.ajlopez.blockchain.vms.eth.TrieStorage;
import com.ajlopez.blockchain.vms.eth.TrieStorageProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class TopExecutionContextTest {
    @Test
    public void getZeroBalanceFromNewAccount() {
        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

        Coin result = executionContext.getBalance(new Address(new byte[] { 0x01, 0x02 }));
        
        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ZERO, result);
    }

    @Test
    public void getNullCodeFromUnknownAccount() {
        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

        Assert.assertNull(executionContext.getCode(FactoryHelper.createRandomAddress()));
    }

    @Test
    public void getNullCodeFromAccountWithoutCode() {
        AccountStore accountStore = new AccountStore(new Trie());
        Account account = new Account();
        Address address = FactoryHelper.createRandomAddress();
        accountStore.putAccount(address, account);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

        Assert.assertNull(executionContext.getCode(address));
    }

    @Test
    public void getCodeFromAccountWithCode() {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        AccountStore accountStore = new AccountStore(new Trie());
        byte[] code = FactoryHelper.createRandomBytes(100);
        Hash codeHash = FactoryHelper.createRandomHash();
        codeStore.putCode(codeHash, code);

        Account account = new Account(Coin.ZERO, 0, codeHash, null);
        Address address = FactoryHelper.createRandomAddress();
        accountStore.putAccount(address, account);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);

        byte[] result = executionContext.getCode(address);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(code, result);
    }

    @Test
    public void getNullCodeHashFromNewAccount() {
        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

        Hash result = executionContext.getCodeHash(new Address(new byte[] { 0x01, 0x02 }));

        Assert.assertNull(result);
    }

    @Test
    public void getNullStorageHashFromNewAccount() {
        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

        Hash result = executionContext.getAccountState(new Address(new byte[] { 0x01, 0x02 })).getStorageHash();

        Assert.assertNull(result);
    }

    @Test
    public void setAndGetCodeHashFromNewAccount() {
        Hash codeHash = FactoryHelper.createRandomHash();
        Address address = FactoryHelper.createRandomAddress();

        AccountStore accountStore = new AccountStore(new Trie());

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

        executionContext.setCodeHash(address, codeHash);

        Hash result = executionContext.getCodeHash(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(codeHash, result);
    }

    @Test
    public void getEmptyStorageFromNewAccount() {
        AccountStore accountStore = new AccountStore(new Trie());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        TrieStorageProvider trieStorageProvider = new TrieStorageProvider(trieStore);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, trieStorageProvider, null);

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
        TrieStorageProvider trieStorageProvider = new TrieStorageProvider(trieStore);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, trieStorageProvider, null);

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
    public void getStorageFromNewAccountAndSetKeyValueAndRollback() {
        Address address = FactoryHelper.createRandomAddress();
        AccountStore accountStore = new AccountStore(new Trie());
        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);
        TrieStorageProvider trieStorageProvider = new TrieStorageProvider(trieStore);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, trieStorageProvider, null);

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

        executionContext.rollback();

        Assert.assertNull(executionContext.getAccountState(address).getStorageHash());

        Account account = accountStore.getAccount(address);

        Assert.assertNotNull(account);
        Assert.assertNull(account.getStorageHash());
    }

    @Test
    public void getBalanceFromAccountAndCommitDoesNotChangeStore() {
        AccountStore accountStore = new AccountStore(new Trie());
        Account account = new Account(Coin.TEN, 42, null, null);
        Address address = FactoryHelper.createRandomAddress();

        accountStore.putAccount(address, account);

        Hash originalHash = accountStore.getRootHash();

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

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
    public void getZeroBalanceFromAccountAndCommitDoesNotChangeStore() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Hash originalHash = accountStore.getRootHash();

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

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
    public void incrementNonceAccount() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();

        Account account = new Account(Coin.fromUnsignedLong(1000), 41, null, null);
        accountStore.putAccount(address, account);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

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

        Account account = new Account(Coin.fromUnsignedLong(1000), 41, null, null);
        accountStore.putAccount(address, account);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

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

        Account account = new Account(Coin.fromUnsignedLong(1000), 41, null, null);
        accountStore.putAccount(address, account);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

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

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

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

        Account sender = new Account(Coin.fromUnsignedLong(1000), 42, null, null);
        accountStore.putAccount(senderAddress, sender);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

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
    public void transferToAccountAndCommit() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(Coin.fromUnsignedLong(1000), 42, null, null);
        accountStore.putAccount(senderAddress, sender);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

        executionContext.transfer(senderAddress, receiverAddress, Coin.fromUnsignedLong(100));
        executionContext.commit();

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
    public void transferToAccountAndRollback() {
        AccountStore accountStore = new AccountStore(new Trie());
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(Coin.fromUnsignedLong(1000), 42, null, null);
        accountStore.putAccount(senderAddress, sender);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);

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
}
