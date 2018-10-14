package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.messages.Message;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 14/10/2018.
 */
public class NodeProcessorTest {
    @Test
    public void processBlockMessage() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = new NodeProcessor(blockChain);

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        Semaphore sem = new Semaphore(0, true);

        nodeProcessor.onEmpty(() -> {
            sem.release();
        });

        nodeProcessor.postMessage(null, message);
        nodeProcessor.start();

        sem.acquire();

        nodeProcessor.stop();

        Block result = blockChain.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }
}
