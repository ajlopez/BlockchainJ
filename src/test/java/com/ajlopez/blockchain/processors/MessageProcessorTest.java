package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.messages.BlockMessage;
import com.ajlopez.blockchain.messages.Message;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class MessageProcessorTest {
    @Test
    public void processBlockMessage() {
        BlockProcessor blockProcessor = new BlockProcessor();
        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        MessageProcessor processor = new MessageProcessor(blockProcessor);

        processor.processMessage(message);

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }
}
