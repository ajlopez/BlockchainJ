package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 05/06/2020.
 */
public class KeyValueStoresTest {
    @Test
    public void getValues() throws IOException {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value1 = FactoryHelper.createRandomBytes(42);
        byte[] value2 = FactoryHelper.createRandomBytes(42);
        byte[] value3 = FactoryHelper.createRandomBytes(42);
        byte[] value4 = FactoryHelper.createRandomBytes(42);
        byte[] value5 = FactoryHelper.createRandomBytes(42);

        KeyValueStores keyValueStores = new MemoryKeyValueStores();

        keyValueStores.getBlockKeyValueStore().setValue(key, value1);
        keyValueStores.getAccountKeyValueStore().setValue(key, value2);
        keyValueStores.getCodeKeyValueStore().setValue(key, value3);
        keyValueStores.getStorageKeyValueStore().setValue(key, value4);
        keyValueStores.getBlockInformationKeyValueStore().setValue(key, value5);

        Assert.assertArrayEquals(value1, keyValueStores.getValue(KeyValueStoreType.BLOCKS, key));
        Assert.assertArrayEquals(value2, keyValueStores.getValue(KeyValueStoreType.ACCOUNTS, key));
        Assert.assertArrayEquals(value3, keyValueStores.getValue(KeyValueStoreType.CODES, key));
        Assert.assertArrayEquals(value4, keyValueStores.getValue(KeyValueStoreType.STORAGE, key));
        Assert.assertArrayEquals(value5, keyValueStores.getValue(KeyValueStoreType.BLOCKS_INFORMATION, key));
    }
}
