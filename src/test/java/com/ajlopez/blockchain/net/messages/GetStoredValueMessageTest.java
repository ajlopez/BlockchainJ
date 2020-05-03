package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.StoreType;
import com.ajlopez.blockchain.store.TrieType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 03/05/2020.
 */
public class GetStoredValueMessageTest {
    @Test
    public void createMessage() {
        StoreType storeType = StoreType.BLOCKS;
        byte[] key = FactoryHelper.createRandomBytes(32);

        GetStoredValueMessage message = new GetStoredValueMessage(storeType, key);

        Assert.assertEquals(storeType, message.getStoreType());
        Assert.assertArrayEquals(key, message.getKey());
        Assert.assertFalse(message.isPriorityMessage());
    }
}
