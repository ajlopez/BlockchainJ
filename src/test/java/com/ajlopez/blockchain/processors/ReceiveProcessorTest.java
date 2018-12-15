package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.TransactionMessage;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 28/01/2018.
 */
public class ReceiveProcessorTest {
    @Test
    public void processBlockMessage() throws InterruptedException {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Block block = new Block(0, null, HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000);
        Message message = new BlockMessage(block);

        MessageProcessor messageProcessor = FactoryHelper.createMessageProcessor(blockProcessor);

        ReceiveProcessor processor = new ReceiveProcessor(messageProcessor);

        Semaphore sem = new Semaphore(0, true);

        processor.onEmpty(() -> {
            sem.release();
        });

        processor.postMessage(null, message);
        processor.start();

        sem.acquire();

        processor.stop();

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void processTwoConsecutiveBlockMessages() throws InterruptedException {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Block genesis = new Block(0, null, HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000);
        Block block1 = new Block(1, genesis.getHash(), HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000);

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        MessageProcessor messageProcessor = FactoryHelper.createMessageProcessor(blockProcessor);

        ReceiveProcessor processor = new ReceiveProcessor(messageProcessor);

        Semaphore sem = new Semaphore(0, true);

        processor.onEmpty(() -> {
            sem.release();
        });

        processor.postMessage(null, message0);
        processor.postMessage(null, message1);

        processor.start();

        sem.acquire();

        processor.stop();

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block1.getHash(), result.getHash());
    }

    @Test
    public void processTwoConsecutiveBlockMessagesOutOfOrder() throws InterruptedException {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Block genesis = new Block(0, null, HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000);
        Block block1 = new Block(1, genesis.getHash(), HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000);

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        MessageProcessor messageProcessor = FactoryHelper.createMessageProcessor(blockProcessor);

        ReceiveProcessor processor = new ReceiveProcessor(messageProcessor);

        Semaphore sem = new Semaphore(0, true);

        processor.onEmpty(() -> {
            sem.release();
        });

        processor.postMessage(null, message1);
        processor.postMessage(null, message0);

        processor.start();

        sem.acquire();

        processor.stop();

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block1.getHash(), result.getHash());
    }

    @Test
    public void processTenRepeatedBlockMessage() throws InterruptedException {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Block block = new Block(0, null, HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000);
        Message message = new BlockMessage(block);

        MessageProcessor messageProcessor = FactoryHelper.createMessageProcessor(blockProcessor);

        ReceiveProcessor processor = new ReceiveProcessor(messageProcessor);

        Semaphore sem = new Semaphore(0, true);

        processor.onEmpty(() -> {
            sem.release();
        });

        for (int k = 0; k < 10; k++)
            processor.postMessage(null, message);

        processor.start();

        sem.acquire();

        processor.stop();

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void processTransactionMessage() throws InterruptedException {
        TransactionPool pool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(pool);

        Transaction transaction = FactoryHelper.createTransaction(100);
        Message message = new TransactionMessage(transaction);

        MessageProcessor messageProcessor = FactoryHelper.createMessageProcessor(transactionProcessor);

        ReceiveProcessor processor = new ReceiveProcessor(messageProcessor);

        Semaphore sem = new Semaphore(0, true);
        processor.onEmpty(() -> {
            sem.release();
        });

        processor.postMessage(null, message);
        processor.start();

        sem.acquire();

        processor.stop();

        List<Transaction> transactions = pool.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());

        Transaction result = transactions.get(0);

        Assert.assertNotNull(result);
        Assert.assertEquals(transaction.getHash(), result.getHash());
    }
}
