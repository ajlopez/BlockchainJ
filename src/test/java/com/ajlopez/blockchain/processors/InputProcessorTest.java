package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.messages.BlockMessage;
import com.ajlopez.blockchain.messages.Message;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 28/01/2018.
 */
public class InputProcessorTest {
    @Test
    public void processBlockMessage() throws InterruptedException {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        MessageProcessor messageProcessor = new MessageProcessor(blockProcessor, null);

        InputProcessor processor = new InputProcessor(messageProcessor);

        Semaphore sem = new Semaphore(0, true);

        processor.onEmpty(() -> {
            sem.release();
        });
        processor.postMessage(message);
        processor.start();

        sem.acquire();

        processor.stop();

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void processTenRepeatedBlockMessage() throws InterruptedException {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        MessageProcessor messageProcessor = new MessageProcessor(blockProcessor, null);

        InputProcessor processor = new InputProcessor(messageProcessor);

        Semaphore sem = new Semaphore(0, true);

        processor.onEmpty(() -> {
            sem.release();
        });

        for (int k = 0; k < 10; k++)
            processor.postMessage(message);

        processor.start();

        sem.acquire();

        processor.stop();

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }
}
