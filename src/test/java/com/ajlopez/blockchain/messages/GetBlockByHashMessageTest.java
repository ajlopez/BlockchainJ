package com.ajlopez.blockchain.messages;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 29/01/2018.
 */
public class GetBlockByHashMessageTest {
    @Test
    public void createWithHash() {
        Hash hash = HashUtilsTest.generateRandomHash();

        GetBlockByHashMessage message = new GetBlockByHashMessage(hash);

        Assert.assertEquals(MessageType.GET_BLOCK_BY_HASH, message.getMessageType());
        Assert.assertArrayEquals(hash.getBytes(), message.getPayload());
        Assert.assertEquals(hash, message.getHash());
    }
}
