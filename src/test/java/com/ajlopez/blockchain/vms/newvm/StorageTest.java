package com.ajlopez.blockchain.vms.newvm;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 20/11/2017.
 */
public class StorageTest {
    @Test
    public void getUndefinedValue() throws IOException {
        Storage storage = new Storage();

        Assert.assertNull(storage.getValue(new byte[] { 0x01, 0x02, 0x03 }));
    }

    @Test
    public void setAndGetValue() throws IOException {
        byte[] key = new byte[] { 0x01, 0x02, 0x03 };
        byte[] value = new byte[] { 0x04, 0x05 };

        Storage storage = new Storage();

        storage.setValue(key, value);
        Assert.assertArrayEquals(value, storage.getValue(key));
    }
}
