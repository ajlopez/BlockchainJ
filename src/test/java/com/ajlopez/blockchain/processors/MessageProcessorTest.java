package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.messages.*;
import com.ajlopez.blockchain.test.simples.SimpleOutputChannel;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class MessageProcessorTest {
    @Test
    public void processBlockMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        MessageProcessor processor = new MessageProcessor(blockProcessor, null, null);

        processor.processMessage(message, null);

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void processGetBlockByHashMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Block block = new Block(0, null);
        Message blockMessage = new BlockMessage(block);

        MessageProcessor processor = new MessageProcessor(blockProcessor, null, null);

        processor.processMessage(blockMessage, null);

        Message message = new GetBlockByHashMessage(block.getHash());
        SimpleOutputChannel output = new SimpleOutputChannel();

        processor.processMessage(message, output);

        Message result = output.getMessage();

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.BLOCK, result.getMessageType());

        BlockMessage bmessage = (BlockMessage)result;

        Assert.assertNotNull(bmessage.getBlock());
        Assert.assertEquals(block.getHash(), bmessage.getBlock().getHash());
    }

    @Test
    public void processGetBlockByNumberMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Block block = new Block(0, null);
        Message blockMessage = new BlockMessage(block);

        MessageProcessor processor = new MessageProcessor(blockProcessor, null, null);

        processor.processMessage(blockMessage, null);

        Message message = new GetBlockByNumberMessage(block.getNumber());
        SimpleOutputChannel output = new SimpleOutputChannel();

        processor.processMessage(message, output);

        Message result = output.getMessage();

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.BLOCK, result.getMessageType());

        BlockMessage bmessage = (BlockMessage)result;

        Assert.assertNotNull(bmessage.getBlock());
        Assert.assertEquals(block.getHash(), bmessage.getBlock().getHash());
    }

    @Test
    public void processTransactionMessage() {
        TransactionPool pool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(pool);

        Transaction transaction = FactoryHelper.createTransaction(100);
        Message message = new TransactionMessage(transaction);

        MessageProcessor processor = new MessageProcessor(null, transactionProcessor, null);

        processor.processMessage(message, null);

        List<Transaction> transactions = pool.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());

        Transaction result = transactions.get(0);

        Assert.assertNotNull(result);
        Assert.assertEquals(transaction.getHash(), result.getHash());
    }

    @Test
    public void processStatusMessage() {
        PeerProcessor peerProcessor = new PeerProcessor();
        MessageProcessor processor = new MessageProcessor(null, null, peerProcessor);
        Hash nodeId = HashUtilsTest.generateRandomHash();
        Message message = new StatusMessage(nodeId, 1, 100);

        processor.processMessage(message, null);

        Assert.assertEquals(100, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(100, peerProcessor.getPeerBestBlockNumber(nodeId));
    }
}
