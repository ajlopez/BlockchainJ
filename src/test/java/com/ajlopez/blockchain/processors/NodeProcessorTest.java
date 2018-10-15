package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.TransactionMessage;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 14/10/2018.
 */
public class NodeProcessorTest {
    @Test
    public void createWithPeer() {
        BlockChain blockChain = new BlockChain();
        Peer peer = FactoryHelper.createPeer();

        NodeProcessor nodeProcessor = new NodeProcessor(peer, blockChain);

        Assert.assertSame(peer, nodeProcessor.getPeer());
    }

    @Test
    public void processBlockMessage() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);

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

    @Test
    public void processTenRepeatedBlockMessages() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        Semaphore sem = new Semaphore(0, true);

        nodeProcessor.onEmpty(() -> {
            sem.release();
        });

        for (int k = 0; k < 10; k++)
            nodeProcessor.postMessage(null, message);

        nodeProcessor.start();

        sem.acquire();

        nodeProcessor.stop();

        Block result = blockChain.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void processTwoConsecutiveBlockMessages() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);

        Block genesis = new Block(0, null);
        Block block1 = new Block(1, genesis.getHash());

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        Semaphore sem = new Semaphore(0, true);

        nodeProcessor.onEmpty(() -> {
            sem.release();
        });

        nodeProcessor.postMessage(null, message0);
        nodeProcessor.postMessage(null, message1);
        nodeProcessor.start();

        sem.acquire();

        nodeProcessor.stop();

        Block result = blockChain.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block1.getHash(), result.getHash());
    }

    @Test
    public void processTwoConsecutiveBlockMessagesOutOfOrder() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);

        Block genesis = new Block(0, null);
        Block block1 = new Block(1, genesis.getHash());

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        Semaphore sem = new Semaphore(0, true);

        nodeProcessor.onEmpty(() -> {
            sem.release();
        });

        nodeProcessor.postMessage(null, message1);
        nodeProcessor.postMessage(null, message0);
        nodeProcessor.start();

        sem.acquire();

        nodeProcessor.stop();

        Block result = blockChain.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block1.getHash(), result.getHash());
    }

    @Test
    public void processTransactionMessage() throws InterruptedException {
        Transaction transaction = FactoryHelper.createTransaction(100);
        Message message = new TransactionMessage(transaction);

        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);

        Semaphore sem = new Semaphore(0, true);
        nodeProcessor.onEmpty(() -> {
            sem.release();
        });

        nodeProcessor.postMessage(null, message);
        nodeProcessor.start();

        sem.acquire();

        nodeProcessor.stop();

        List<Transaction> transactions = nodeProcessor.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());

        Transaction result = transactions.get(0);

        Assert.assertNotNull(result);
        Assert.assertEquals(transaction.getHash(), result.getHash());
    }
}
