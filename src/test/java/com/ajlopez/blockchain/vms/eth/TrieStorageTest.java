package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.AccountState;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 17/02/2019.
 */
public class TrieStorageTest {
    @Test
    public void getZeroIfUndefinedValue() {
        Storage storage = new TrieStorage(new Trie());

        DataWord result = storage.getValue(DataWord.fromHexadecimalString("0102"));

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ZERO, result);
    }

    @Test
    public void setAndGetValue() {
        Storage storage = new TrieStorage(new Trie());
        DataWord address = DataWord.fromHexadecimalString("0x010203");
        DataWord value = DataWord.fromHexadecimalString("2a");

        storage.setValue(address, value);

        DataWord result = storage.getValue(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(value, result);
    }

    @Test
    public void setResetAndGetValue() {
        Storage storage = new TrieStorage(new Trie());
        DataWord address = DataWord.fromHexadecimalString("0x010203");
        DataWord value = DataWord.fromHexadecimalString("2a");

        storage.setValue(address, value);
        storage.setValue(address, DataWord.ONE);

        DataWord result = storage.getValue(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ONE, result);
    }

    @Test
    public void setResetToZeroAndGetValue() {
        TrieStorage storage = new TrieStorage(new Trie());

        Hash initialHash = storage.getRootHash();

        DataWord address = DataWord.fromHexadecimalString("0x010203");
        DataWord value = DataWord.fromHexadecimalString("2a");

        storage.setValue(address, value);
        storage.setValue(address, DataWord.fromUnsignedInteger(0));

        DataWord result = storage.getValue(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ZERO, result);
        Assert.assertEquals(initialHash, storage.getRootHash());
    }

    @Test
    public void setAndGetValueAndCommit() {
        TrieStore store = new TrieStore(new HashMapStore());
        TrieStorage storage = new TrieStorage(new Trie(store));

        DataWord address = DataWord.fromHexadecimalString("0x010203");
        DataWord value = DataWord.fromHexadecimalString("2a");

        storage.setValue(address, value);

        DataWord result = storage.getValue(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(value, result);

        storage.commit();

        Trie trie2 = store.retrieve(storage.getRootHash());

        Assert.assertNotNull(trie2);

        TrieStorage storage2 = new TrieStorage(trie2);

        DataWord result2 = storage2.getValue(address);

        Assert.assertNotNull(result2);
        Assert.assertEquals(value, result2);
    }
}
