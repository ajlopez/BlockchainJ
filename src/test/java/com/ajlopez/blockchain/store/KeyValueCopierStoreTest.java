package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 19/04/2020.
 */
public class KeyValueCopierStoreTest {
    @Test
    public void copyKeyValues() throws IOException {
        KeyValueStore originalStore = new HashMapStore();
        KeyValueStore newStore = new HashMapStore();

        byte[][]keys = new byte[16][];
        byte[][]values = new byte[16][];

        for (int k = 0; k < keys.length; k++) {
            keys[k] = FactoryHelper.createRandomBytes(32);
            values[k] = FactoryHelper.createRandomBytes(42);

            originalStore.setValue(keys[k], values[k]);
        }

        for (int k = 0; k < keys.length; k++)
            Assert.assertNull(newStore.getValue(keys[k]));

        KeyValueCopierStore copierStore = new KeyValueCopierStore(originalStore, newStore);

        for (int k = 0; k < keys.length; k++)
            Assert.assertArrayEquals(values[k], copierStore.getValue(keys[k]));

        for (int k = 0; k < keys.length; k++)
            Assert.assertArrayEquals(values[k], newStore.getValue(keys[k]));
    }
}
