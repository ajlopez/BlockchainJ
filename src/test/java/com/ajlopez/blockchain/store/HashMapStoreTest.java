package com.ajlopez.blockchain.store;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 06/01/2018.
 */
public class HashMapStoreTest {
    @Test
    public void getNullForUndefinedKey() {
        HashMapStore store = new HashMapStore();

        Assert.assertNull(store.getValue(new byte[12]));
    }

    @Test
    public void setAndGetValue() {
        HashMapStore store = new HashMapStore();

        byte[] key = new byte[] { 0x01, 0x02, 0x03 };
        byte[] value = new byte[] { 0x04, 0x05, 0x06 };

        store.setValue(key, value);
        Assert.assertArrayEquals(value, store.getValue(key));
    }
}
