package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import com.ajlopez.blockchain.vms.eth.TrieStorage;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ajlopez on 19/07/2020.
 */
public class WorldStateCopierTest {
    @Test
    public void copyOneAccount() throws IOException {
        KeyValueStores sourceKeyValueStores = new MemoryKeyValueStores();
        Stores sourceStores = new Stores(sourceKeyValueStores);
        AccountStore accountStore = sourceStores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);

        Address address = FactoryHelper.createRandomAddress();
        Account account = new Account(Coin.TEN, 42, 0, null, null);

        accountStore.putAccount(address, account);
        accountStore.save();

        Hash rootHash = accountStore.getRootHash();

        KeyValueStores targetKeyValueStores = new MemoryKeyValueStores();
        Stores targetStores = new Stores(targetKeyValueStores);

        WorldStateCopier worldStateCopier = new WorldStateCopier(sourceStores, targetStores, rootHash);

        worldStateCopier.process();

        TrieStore targetAccountTrieStore = targetStores.getAccountTrieStore();
        Assert.assertTrue(targetAccountTrieStore.exists(rootHash));

        AccountStore targetAccountStore = targetStores.getAccountStoreProvider().retrieve(rootHash);

        Account result = targetAccountStore.getAccount(address);

        Assert.assertNotNull(result);

        Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));
    }

    @Test
    public void copyOneAccountWithCode() throws IOException {
        byte[] code = FactoryHelper.createRandomBytes(100);
        Hash codeHash = HashUtils.calculateHash(code);

        KeyValueStores sourceKeyValueStores = new MemoryKeyValueStores();
        Stores sourceStores = new Stores(sourceKeyValueStores);
        AccountStore accountStore = sourceStores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);
        sourceStores.getCodeStore().putCode(codeHash, code);

        Address address = FactoryHelper.createRandomAddress();
        Account account = new Account(Coin.TEN, 42, code.length, codeHash, null);

        accountStore.putAccount(address, account);
        accountStore.save();

        Hash rootHash = accountStore.getRootHash();

        KeyValueStores targetKeyValueStores = new MemoryKeyValueStores();
        Stores targetStores = new Stores(targetKeyValueStores);

        WorldStateCopier worldStateCopier = new WorldStateCopier(sourceStores, targetStores, rootHash);

        worldStateCopier.process();

        TrieStore targetAccountTrieStore = targetStores.getAccountTrieStore();
        Assert.assertTrue(targetAccountTrieStore.exists(rootHash));

        AccountStore targetAccountStore = targetStores.getAccountStoreProvider().retrieve(rootHash);

        Account result = targetAccountStore.getAccount(address);

        Assert.assertNotNull(result);

        Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));

        byte[] result2 = targetStores.getCodeStore().getCode(codeHash);

        Assert.assertNotNull(result2);
        Assert.assertArrayEquals(code, result2);
    }

    @Test
    public void copyOneHundredAccounts() throws IOException {
        Random random = new Random();

        KeyValueStores sourceKeyValueStores = new MemoryKeyValueStores();
        Stores sourceStores = new Stores(sourceKeyValueStores);
        AccountStore accountStore = sourceStores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);

        List<Address> addresses = new ArrayList<>();
        List<Account> accounts = new ArrayList<>();

        for (int k = 0; k < 100; k++) {
            Address address = FactoryHelper.createRandomAddress();
            Account account = new Account(Coin.fromUnsignedLong(random.nextInt(1000)), random.nextInt(1000), 0, null, null);

            addresses.add(address);
            accounts.add(account);

            accountStore.putAccount(address, account);
        }

        accountStore.save();

        Hash rootHash = accountStore.getRootHash();

        KeyValueStores targetKeyValueStores = new MemoryKeyValueStores();
        Stores targetStores = new Stores(targetKeyValueStores);

        WorldStateCopier worldStateCopier = new WorldStateCopier(sourceStores, targetStores, rootHash);

        worldStateCopier.process();

        TrieStore targetAccountTrieStore = targetStores.getAccountTrieStore();
        Assert.assertTrue(targetAccountTrieStore.exists(rootHash));

        AccountStore targetAccountStore = targetStores.getAccountStoreProvider().retrieve(rootHash);

        for (int k = 0; k < 100; k++) {
            Address address = addresses.get(k);
            Account account = accounts.get(k);

            Account result = targetAccountStore.getAccount(address);

            Assert.assertNotNull(result);

            Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));
        }
    }

    @Test
    public void copyOneHundredAccountsAndOneHundredStorageCells() throws IOException {
        Random random = new Random();

        KeyValueStores sourceKeyValueStores = new MemoryKeyValueStores();
        Stores sourceStores = new Stores(sourceKeyValueStores);
        AccountStore accountStore = sourceStores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);
        TrieStorage trieStorage = sourceStores.getTrieStorageProvider().retrieve(Trie.EMPTY_TRIE_HASH);

        List<Address> addresses = new ArrayList<>();
        List<Account> accounts = new ArrayList<>();
        List<DataWord> keys = new ArrayList<>();
        List<DataWord> values = new ArrayList<>();

        for (int k = 0; k < 100; k++) {
            Address address = FactoryHelper.createRandomAddress();
            Account account = new Account(Coin.fromUnsignedLong(random.nextInt(1000) + 1), random.nextInt(1000), 0, null, null);

            addresses.add(address);
            accounts.add(account);

            accountStore.putAccount(address, account);

            DataWord key = FactoryHelper.createRandomDataWord();
            DataWord value = FactoryHelper.createRandomDataWord();

            keys.add(key);
            values.add(value);

            trieStorage.setValue(key, value);
        }

        trieStorage.commit();

        Address address0 = addresses.get(0);
        Account account0 = accounts.get(0);

        account0 = new Account(account0.getBalance(), account0.getNonce(), 0, null, trieStorage.getRootHash());

        accounts.set(0, account0);

        accountStore.putAccount(address0, account0);

        accountStore.save();

        Hash rootHash = accountStore.getRootHash();

        KeyValueStores targetKeyValueStores = new MemoryKeyValueStores();
        Stores targetStores = new Stores(targetKeyValueStores);

        WorldStateCopier worldStateCopier = new WorldStateCopier(sourceStores, targetStores, rootHash);

        worldStateCopier.process();

        TrieStore targetAccountTrieStore = targetStores.getAccountTrieStore();
        Assert.assertTrue(targetAccountTrieStore.exists(rootHash));

        AccountStore targetAccountStore = targetStores.getAccountStoreProvider().retrieve(rootHash);

        for (int k = 0; k < 100; k++) {
            Address address = addresses.get(k);
            Account account = accounts.get(k);

            Account result = targetAccountStore.getAccount(address);

            Assert.assertNotNull(result);

            Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));
        }

        TrieStore targetStorageTrieStore = targetStores.getStorageTrieStore();
        Assert.assertTrue(targetStorageTrieStore.exists(account0.getStorageHash()));

        TrieStorage targetTrieStorage = targetStores.getTrieStorageProvider().retrieve(account0.getStorageHash());

        for (int k = 0; k < 100; k++)
            Assert.assertEquals(values.get(k), targetTrieStorage.getValue(keys.get(k)));
    }
}
