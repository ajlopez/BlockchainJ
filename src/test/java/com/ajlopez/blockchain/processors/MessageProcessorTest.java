package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.messages.*;
import com.ajlopez.blockchain.test.simples.SimplePeer;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
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
        SimplePeer sender = new SimplePeer(HashUtilsTest.generateRandomPeerId());

        processor.processMessage(message, sender);

        Message result = sender.getLastMessage();

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

        Message getBlockMessage = new GetBlockByNumberMessage(block.getNumber());
        SimplePeer sender = new SimplePeer(HashUtilsTest.generateRandomPeerId());

        processor.processMessage(getBlockMessage, sender);

        Message result = sender.getLastMessage();

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
    public void processStatusMessageAndStartSync() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        PeerProcessor peerProcessor = new PeerProcessor();
        MessageProcessor processor = new MessageProcessor(blockProcessor, null, peerProcessor);
        PeerId peerId = HashUtilsTest.generateRandomPeerId();
        SimplePeer peer = new SimplePeer(peerId);
        Message message = new StatusMessage(peerId, 1, 10);

        processor.processMessage(message, peer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(peerId));

        Assert.assertEquals(11, peer.getMessages().size());

        for (int k = 0; k < 11; k++) {
            Message msg = peer.getMessages().get(k);

            Assert.assertNotNull(msg);
            Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER, msg.getMessageType());

            GetBlockByNumberMessage gmsg = (GetBlockByNumberMessage)msg;

            Assert.assertEquals(k, gmsg.getNumber());
        }
    }

    @Test
    public void processStatusMessageTwiceWithSameHeightAndStartSync() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        PeerProcessor peerProcessor = new PeerProcessor();
        MessageProcessor processor = new MessageProcessor(blockProcessor, null, peerProcessor);
        PeerId peerId = HashUtilsTest.generateRandomPeerId();
        SimplePeer peer = new SimplePeer(peerId);
        Message message = new StatusMessage(peerId, 1, 10);

        processor.processMessage(message, peer);
        processor.processMessage(message, peer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(peerId));

        Assert.assertEquals(11, peer.getMessages().size());

        for (int k = 0; k < 11; k++) {
            Message msg = peer.getMessages().get(k);

            Assert.assertNotNull(msg);
            Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER, msg.getMessageType());

            GetBlockByNumberMessage gmsg = (GetBlockByNumberMessage)msg;

            Assert.assertEquals(k, gmsg.getNumber());
        }
    }

    @Test
    public void processStatusMessageTwiceWithDifferentHeightsAndStartSync() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        PeerProcessor peerProcessor = new PeerProcessor();
        MessageProcessor processor = new MessageProcessor(blockProcessor, null, peerProcessor);
        PeerId peerId = HashUtilsTest.generateRandomPeerId();
        SimplePeer peer = new SimplePeer(peerId);
        Message message1 = new StatusMessage(peerId, 1, 5);
        Message message2 = new StatusMessage(peerId, 1, 10);

        processor.processMessage(message1, peer);
        processor.processMessage(message2, peer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(peerId));

        Assert.assertEquals(11, peer.getMessages().size());

        for (int k = 0; k < 11; k++) {
            Message msg = peer.getMessages().get(k);

            Assert.assertNotNull(msg);
            Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER, msg.getMessageType());

            GetBlockByNumberMessage gmsg = (GetBlockByNumberMessage)msg;

            Assert.assertEquals(k, gmsg.getNumber());
        }
    }
}
