package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 19/01/2018.
 */
public class BlockMessageTest {
    @Test
    public void createWithBlock() {
        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000, coinbase);

        BlockMessage message = new BlockMessage(block);

        Assert.assertEquals(MessageType.BLOCK, message.getMessageType());
        Assert.assertArrayEquals(BlockEncoder.encode(block), message.getPayload());
    }
}
