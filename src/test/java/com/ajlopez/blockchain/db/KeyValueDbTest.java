package com.ajlopez.blockchain.db;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 30/10/2019.
 */
public class KeyValueDbTest {
    @Test
    public void saveAndRetrieveKeyValue() throws IOException {
        KeyValueDb keyValueDb = new KeyValueDb("data1", 32);

        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        keyValueDb.setValue(key, value);

        byte[] result = keyValueDb.getValue(key);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(value, result);
    }

    @Test
    public void retrieveUnknownValueAsNull() throws IOException {
        KeyValueDb keyValueDb = new KeyValueDb("data1", 32);

        byte[] key = FactoryHelper.createRandomBytes(32);

        Assert.assertNull(keyValueDb.getValue(key));
    }
}
