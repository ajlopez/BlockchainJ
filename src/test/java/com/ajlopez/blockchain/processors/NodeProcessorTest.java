package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.*;
import com.ajlopez.blockchain.test.PeerToPeerOutputChannel;
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
    public void processTenBlockMessages() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);
        List<Block> blocks = FactoryHelper.createBlocks(9);

        Semaphore sem = new Semaphore(0, true);

        nodeProcessor.onEmpty(() -> {
            sem.release();
        });

        for (Block block: blocks) {
            Message message = new BlockMessage(block);
            nodeProcessor.postMessage(null, message);
        }

        nodeProcessor.start();

        sem.acquire();

        nodeProcessor.stop();

        Block result = blockChain.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(9, result.getNumber());
        Assert.assertEquals(blocks.get(9).getHash(), result.getHash());
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
    public void processTwoBlockMessagesUsingTwoNodes() throws InterruptedException {
        BlockChain blockChain1 = new BlockChain();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);
        BlockChain blockChain2 = new BlockChain();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);

        PeerToPeerOutputChannel channel = new PeerToPeerOutputChannel(nodeProcessor1.getPeer(), nodeProcessor2.getPeer(), nodeProcessor2);

        nodeProcessor1.connectTo(nodeProcessor2.getPeer(), channel);

        Block genesis = new Block(0, null);
        Block block1 = new Block(1, genesis.getHash());

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        Semaphore sem1 = new Semaphore(0, true);

        nodeProcessor1.onEmpty(() -> {
            sem1.release();
        });

        Semaphore sem2 = new Semaphore(0, true);

        nodeProcessor2.onEmpty(() -> {
            sem2.release();
        });

        nodeProcessor1.postMessage(null, message0);
        nodeProcessor1.postMessage(null, message1);
        nodeProcessor1.start();
        nodeProcessor2.start();

        sem1.acquire();
        sem2.acquire();

        nodeProcessor1.stop();
        nodeProcessor2.stop();

        Block result1 = blockChain1.getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(block1.getHash(), result1.getHash());

        Block result2 = blockChain2.getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(block1.getHash(), result2.getHash());
    }

    @Test
    public void synchronizeTwoNodes() throws InterruptedException {
        List<Block> blocks = FactoryHelper.createBlocks(9);
        Block bestBlock = blocks.get(9);

        BlockChain blockChain1 = new BlockChain();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);
        BlockChain blockChain2 = new BlockChain();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);

        PeerToPeerOutputChannel channel1 = new PeerToPeerOutputChannel(nodeProcessor2.getPeer(), nodeProcessor1.getPeer(), nodeProcessor2);
        PeerToPeerOutputChannel channel2 = new PeerToPeerOutputChannel(nodeProcessor1.getPeer(), nodeProcessor2.getPeer(), nodeProcessor2);

        nodeProcessor1.connectTo(nodeProcessor2.getPeer(), channel2);
        nodeProcessor2.connectTo(nodeProcessor1.getPeer(), channel1);

        Semaphore sem1 = new Semaphore(0, true);

        nodeProcessor1.onEmpty(() -> {
            sem1.release();
        });

        Semaphore sem2 = new Semaphore(0, true);

        nodeProcessor2.onEmpty(() -> {
            sem2.release();
        });

        for (Block block : blocks)
            Assert.assertTrue(blockChain1.connectBlock(block));

        for (int k = 0; k < 10; k++)
            Assert.assertNotNull(blockChain1.getBlockByNumber(k));

        Status status = new Status(nodeProcessor1.getPeer().getId(), 1,9);
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        nodeProcessor1.start();
        nodeProcessor2.start();

        sem1.acquire();
        sem2.acquire();

        nodeProcessor1.stop();
        nodeProcessor2.stop();

        Block result1 = blockChain1.getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = blockChain2.getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());
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
