package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 09/12/2018.
 */
public class MapStorageTest {
    @Test
    public void getZeroIfUndefinedValue() throws IOException {
        Storage storage = new MapStorage();

        DataWord result = storage.getValue(DataWord.fromHexadecimalString("0102"));

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ZERO, result);
    }

    @Test
    public void setAndGetValue() throws IOException {
        Storage storage = new MapStorage();
        DataWord address = DataWord.fromHexadecimalString("0x010203");
        DataWord value = DataWord.fromHexadecimalString("2a");

        storage.setValue(address, value);

        DataWord result = storage.getValue(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(value, result);
    }

    @Test
    public void setZeroAndGetValue() throws IOException {
        Storage storage = new MapStorage();
        DataWord address = DataWord.fromHexadecimalString("0x010203");

        storage.setValue(address, DataWord.ZERO);

        DataWord result = storage.getValue(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ZERO, result);
    }

    @Test
    public void setResetAndGetValue() throws IOException {
        Storage storage = new MapStorage();
        DataWord address = DataWord.fromHexadecimalString("0x010203");
        DataWord value = DataWord.fromHexadecimalString("2a");

        storage.setValue(address, value);
        storage.setValue(address, DataWord.ZERO);

        DataWord result = storage.getValue(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ZERO, result);
    }
}
