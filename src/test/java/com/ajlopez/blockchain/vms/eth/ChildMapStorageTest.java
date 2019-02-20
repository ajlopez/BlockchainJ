package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.crypto.Data;

/**
 * Created by ajlopez on 20/02/2019.
 */
public class ChildMapStorageTest {
    @Test
    public void getUndefinedValue() {
        Storage parentStorage = new MapStorage();
        Storage storage = new ChildMapStorage(parentStorage);

        Assert.assertFalse(((ChildMapStorage) storage).hasValue(DataWord.ZERO));
        Assert.assertEquals(DataWord.ZERO, storage.getValue(DataWord.ONE));
    }

    @Test
    public void getParentDefinedValue() {
        Storage parentStorage = new MapStorage();

        parentStorage.setValue(DataWord.ONE, DataWord.ONE);

        Storage storage = new ChildMapStorage(parentStorage);

        Assert.assertTrue(storage.hasValue(DataWord.ONE));
        Assert.assertEquals(DataWord.ONE, storage.getValue(DataWord.ONE));
    }

    @Test
    public void setAndGetValue() {
        Storage parentStorage = new MapStorage();

        Storage storage = new ChildMapStorage(parentStorage);

        storage.setValue(DataWord.ONE, DataWord.ONE);

        Assert.assertTrue(storage.hasValue(DataWord.ONE));
        Assert.assertEquals(DataWord.ONE, storage.getValue(DataWord.ONE));

        Assert.assertFalse(parentStorage.hasValue(DataWord.ONE));
        Assert.assertEquals(DataWord.ZERO, parentStorage.getValue(DataWord.ONE));    }
}
