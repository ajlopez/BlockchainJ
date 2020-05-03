package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.store.StoreType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 03/05/2020.
 */
public class StoredKeyValueMessageTest {
    @Test
    public void createMessage() {
        StoreType storeType = StoreType.BLOCKS;
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        StoredKeyValueMessage message = new StoredKeyValueMessage(storeType, key, value);

        Assert.assertEquals(MessageType.STORED_KEY_VALUE, message.getMessageType());
        Assert.assertEquals(storeType, message.getStoreType());
        Assert.assertArrayEquals(key, message.getKey());
        Assert.assertArrayEquals(value, message.getValue());
        Assert.assertFalse(message.isPriorityMessage());
    }
}
