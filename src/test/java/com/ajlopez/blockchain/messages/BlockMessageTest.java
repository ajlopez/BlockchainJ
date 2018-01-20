package com.ajlopez.blockchain.messages;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Hash;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 19/01/2018.
 */
public class BlockMessageTest {
    @Test
    public void createWithBlock() {
        Hash hash = HashUtilsTest.generateRandomHash();
        Block block = new Block(1L, hash);

        BlockMessage message = new BlockMessage(block);

        Assert.assertEquals(MessageType.BLOCK, message.getMessageType());
        Assert.assertArrayEquals(BlockEncoder.encode(block), message.getPayload());
    }
}
