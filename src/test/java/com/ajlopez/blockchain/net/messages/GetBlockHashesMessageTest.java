package com.ajlopez.blockchain.net.messages;

import org.junit.Assert;
import org.junit.Test;

public class GetBlockHashesMessageTest {
    @Test
    public void createMessage() {
        GetBlockHashesMessage message = new GetBlockHashesMessage(42, 10, 2);

        Assert.assertEquals(MessageType.GET_BLOCK_HASHES, message.getMessageType());
        Assert.assertEquals(42, message.getBlockHeight());
        Assert.assertEquals(10, message.getNoBlocks());
        Assert.assertEquals(2, message.getBlockGap());
    }
}
