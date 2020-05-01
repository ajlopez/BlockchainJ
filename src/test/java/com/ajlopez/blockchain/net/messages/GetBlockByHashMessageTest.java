package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 29/01/2018.
 */
public class GetBlockByHashMessageTest {
    @Test
    public void createWithHash() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();

        GetBlockByHashMessage message = new GetBlockByHashMessage(hash);

        Assert.assertEquals(MessageType.GET_BLOCK_BY_HASH, message.getMessageType());
        Assert.assertArrayEquals(hash.getBytes(), message.getPayload());
        Assert.assertEquals(hash, message.getHash());
        Assert.assertFalse(message.isPriorityMessage());
    }
}
