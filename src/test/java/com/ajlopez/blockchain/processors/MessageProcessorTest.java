package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.*;
import com.ajlopez.blockchain.test.simples.SimpleMessageChannel;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import javafx.util.Pair;
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

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor);

        processor.processMessage(message, null);

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void processBlockMessageAndRelayBlockToPeers() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Peer sender = FactoryHelper.createPeer();
        SendProcessor outputProcessor = new SendProcessor(sender);
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(sender, channel);

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(message, null);

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());

        expectedMessage(channel, sender, message);
    }

    @Test
    public void processBlockMessageAndRelayBlockToOtherPeers() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Peer sender = FactoryHelper.createPeer();
        SendProcessor sendProcessor = new SendProcessor(sender);

        Peer peer1 = FactoryHelper.createPeer();
        SimpleMessageChannel channel1 = new SimpleMessageChannel();
        sendProcessor.connectToPeer(peer1, channel1);

        Peer peer2 = FactoryHelper.createPeer();
        SimpleMessageChannel channel2 = new SimpleMessageChannel();
        sendProcessor.connectToPeer(peer2, channel2);

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, sendProcessor);

        processor.processMessage(message, peer1);

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());

        List<Pair<Peer, Message>> peerMessages1 = channel1.getPeerMessages();

        Assert.assertNotNull(peerMessages1);
        Assert.assertTrue(peerMessages1.isEmpty());

        expectedMessage(channel2, sender, message);
    }

    @Test
    public void processGetBlockByHashMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        Peer originalSender = FactoryHelper.createPeer();
        Peer sender = FactoryHelper.createPeer();
        SendProcessor sendProcessor = new SendProcessor(sender);

        Block block = new Block(0, null);
        Message blockMessage = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, sendProcessor);

        processor.processMessage(blockMessage, null);

        Message message = new GetBlockByHashMessage(block.getHash());

        SimpleMessageChannel channel = new SimpleMessageChannel();
        sendProcessor.connectToPeer(originalSender, channel);

        processor.processMessage(message, originalSender);

        expectedMessage(channel, sender, blockMessage);
    }

    @Test
    public void processGetUnknownBlockByHashMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        SendProcessor outputProcessor = new SendProcessor(FactoryHelper.createPeer());

        Block block = new Block(0, null);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        Message message = new GetBlockByHashMessage(block.getHash());

        Peer sender = FactoryHelper.createPeer();
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(sender, channel);

        processor.processMessage(message, sender);

        Message result = channel.getLastMessage();

        Assert.assertNull(result);
    }

    @Test
    public void processGetBlockByNumberMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        Peer sender = FactoryHelper.createPeer();
        SendProcessor outputProcessor = new SendProcessor(sender);

        Block block = new Block(0, null);
        Message blockMessage = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(blockMessage, null);

        Message getBlockMessage = new GetBlockByNumberMessage(block.getNumber());
        SimpleMessageChannel channel = new SimpleMessageChannel();
        Peer originalSender = FactoryHelper.createPeer();
        outputProcessor.connectToPeer(originalSender, channel);

        processor.processMessage(getBlockMessage, originalSender);

        expectedMessage(channel, sender, blockMessage);
    }

    @Test
    public void processGetUnknownBlockByNumberMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        SendProcessor outputProcessor = new SendProcessor(FactoryHelper.createPeer());

        Block block = new Block(0, null);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        Message getBlockMessage = new GetBlockByNumberMessage(block.getNumber());
        Peer sender = FactoryHelper.createPeer();
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(sender, channel);

        processor.processMessage(getBlockMessage, sender);

        Message result = channel.getLastMessage();

        Assert.assertNull(result);
    }

    @Test
    public void processTransactionMessage() {
        TransactionPool pool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(pool);

        Transaction transaction = FactoryHelper.createTransaction(100);
        Message message = new TransactionMessage(transaction);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(transactionProcessor);

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
    public void processTransactionMessageAndRelayToPeers() {
        TransactionPool pool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(pool);

        Transaction transaction = FactoryHelper.createTransaction(100);
        Message message = new TransactionMessage(transaction);

        Peer sender = FactoryHelper.createPeer();
        SendProcessor outputProcessor = new SendProcessor(sender);
        SimpleMessageChannel channel = new SimpleMessageChannel();
        Peer receiver = FactoryHelper.createPeer();
        outputProcessor.connectToPeer(receiver, channel);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(transactionProcessor, outputProcessor);

        processor.processMessage(message, FactoryHelper.createPeer());

        List<Transaction> transactions = pool.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(transaction, transactions.get(0));

        expectedMessage(channel, sender, message);
    }

    @Test
    public void processStatusMessageAndStartSync() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        PeerProcessor peerProcessor = new PeerProcessor();
        SendProcessor outputProcessor = new SendProcessor(FactoryHelper.createPeer());

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        Peer receiver = FactoryHelper.createPeer();
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(receiver, channel);

        Message message = new StatusMessage(new Status(receiver.getId(), 1, 10));

        processor.processMessage(message, receiver);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(receiver.getId()));

        Assert.assertEquals(11, channel.getPeerMessages().size());

        for (int k = 0; k < 11; k++) {
            Message msg = channel.getPeerMessages().get(k).getValue();

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
        Peer firstPeer = FactoryHelper.createPeer();
        SendProcessor outputProcessor = new SendProcessor(firstPeer);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        Peer secondPeer = FactoryHelper.createPeer();
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(secondPeer, channel);

        Message message = new StatusMessage(new Status(secondPeer.getId(), 1, 10));

        processor.processMessage(message, secondPeer);
        processor.processMessage(message, secondPeer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(secondPeer.getId()));

        Assert.assertEquals(11, channel.getPeerMessages().size());

        for (int k = 0; k < 11; k++) {
            Assert.assertEquals(firstPeer, channel.getPeerMessages().get(k).getKey());
            Message msg = channel.getPeerMessages().get(k).getValue();

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
        Peer firstPeer = FactoryHelper.createPeer();
        SendProcessor outputProcessor = new SendProcessor(firstPeer);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        Peer secondPeer = FactoryHelper.createPeer();
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(secondPeer, channel);

        Message message1 = new StatusMessage(new Status(secondPeer.getId(), 1, 5));
        Message message2 = new StatusMessage(new Status(secondPeer.getId(), 1, 10));

        processor.processMessage(message1, secondPeer);
        processor.processMessage(message2, secondPeer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(secondPeer.getId()));

        Assert.assertEquals(11, channel.getPeerMessages().size());

        for (int k = 0; k < 11; k++) {
            Assert.assertEquals(firstPeer, channel.getPeerMessages().get(k).getKey());
            Message msg = channel.getPeerMessages().get(k).getValue();

            Assert.assertNotNull(msg);
            Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER, msg.getMessageType());

            GetBlockByNumberMessage gmsg = (GetBlockByNumberMessage)msg;

            Assert.assertEquals(k, gmsg.getNumber());
        }
    }

    public static void expectedMessage(SimpleMessageChannel channel, Peer expectedSender, Message expectedMessage) {
        List<Pair<Peer,Message>> peerMessages = channel.getPeerMessages();

        Assert.assertNotNull(peerMessages);
        Assert.assertEquals(1, peerMessages.size());

        Peer sender = peerMessages.get(0).getKey();

        Assert.assertNotNull(sender);
        Assert.assertEquals(expectedSender, sender);

        Message message = peerMessages.get(0).getValue();

        Assert.assertNotNull(message);
        Assert.assertArrayEquals(MessageEncoder.encode(expectedMessage), MessageEncoder.encode(message));
    }
}
