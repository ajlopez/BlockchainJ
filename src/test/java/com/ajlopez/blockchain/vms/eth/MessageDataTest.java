package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 24/12/2018.
 */
public class MessageDataTest {
    @Test
    public void createMessageData() {
        Address address = FactoryHelper.createRandomAddress();
        Address origin = FactoryHelper.createRandomAddress();
        Address caller = FactoryHelper.createRandomAddress();
        DataWord value = DataWord.ONE;
        long gas = 42;
        byte[] data = FactoryHelper.createRandomBytes(10);

        MessageData messageData = new MessageData(address, origin, caller, value, gas, data);

        Assert.assertEquals(address, messageData.getAddress());
        Assert.assertEquals(origin, messageData.getOrigin());
        Assert.assertEquals(caller, messageData.getCaller());
        Assert.assertEquals(value, messageData.getValue());
        Assert.assertEquals(gas, messageData.getGas());
        Assert.assertArrayEquals(data, messageData.getData());
    }
}
