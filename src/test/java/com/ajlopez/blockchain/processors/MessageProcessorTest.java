package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.*;
import com.ajlopez.blockchain.test.simples.SimpleOutputChannel;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
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

        OutputProcessor outputProcessor = new OutputProcessor();
        Peer sender = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.connectToPeer(sender, channel);

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(message, null);

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());

        List<Message> messages = channel.getMessages();

        Assert.assertNotNull(messages);
        Assert.assertEquals(1, messages.size());

        Message outputMessage = messages.get(0);

        Assert.assertNotNull(outputMessage);
        Assert.assertEquals(MessageType.BLOCK, outputMessage.getMessageType());
        Assert.assertEquals(block, ((BlockMessage)outputMessage).getBlock());
    }

    @Test
    public void processBlockMessageAndRelayBlockToOtherPeers() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        OutputProcessor outputProcessor = new OutputProcessor();

        Peer peer1 = FactoryHelper.createPeer();
        SimpleOutputChannel channel1 = new SimpleOutputChannel();
        outputProcessor.connectToPeer(peer1, channel1);

        Peer peer2 = FactoryHelper.createPeer();
        SimpleOutputChannel channel2 = new SimpleOutputChannel();
        outputProcessor.connectToPeer(peer2, channel2);

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(message, peer1);

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());

        List<Message> messages1 = channel1.getMessages();

        Assert.assertNotNull(messages1);
        Assert.assertEquals(0, messages1.size());

        List<Message> messages2 = channel2.getMessages();

        Assert.assertNotNull(messages2);
        Assert.assertEquals(1, messages2.size());

        Message outputMessage = messages2.get(0);

        Assert.assertNotNull(outputMessage);
        Assert.assertEquals(MessageType.BLOCK, outputMessage.getMessageType());
        Assert.assertEquals(block, ((BlockMessage)outputMessage).getBlock());
    }

    @Test
    public void processGetBlockByHashMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        OutputProcessor outputProcessor = new OutputProcessor();

        Block block = new Block(0, null);
        Message blockMessage = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(blockMessage, null);

        Message message = new GetBlockByHashMessage(block.getHash());

        Peer sender = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.connectToPeer(sender, channel);

        processor.processMessage(message, sender);

        Message result = channel.getMessage();

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.BLOCK, result.getMessageType());

        BlockMessage bmessage = (BlockMessage)result;

        Assert.assertNotNull(bmessage.getBlock());
        Assert.assertEquals(block.getHash(), bmessage.getBlock().getHash());
    }

    @Test
    public void processGetBlockByNumberMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        OutputProcessor outputProcessor = new OutputProcessor();

        Block block = new Block(0, null);
        Message blockMessage = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(blockMessage, null);

        Message getBlockMessage = new GetBlockByNumberMessage(block.getNumber());
        Peer sender = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.connectToPeer(sender, channel);

        processor.processMessage(getBlockMessage, sender);

        Message result = channel.getMessage();

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

        OutputProcessor outputProcessor = new OutputProcessor();
        Peer sender = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.connectToPeer(sender, channel);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(transactionProcessor, outputProcessor);

        processor.processMessage(message, null);

        List<Transaction> transactions = pool.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(transaction, transactions.get(0));

        List<Message> messages = channel.getMessages();

        Assert.assertNotNull(messages);
        Assert.assertEquals(1, messages.size());

        Message outputMessage = messages.get(0);

        Assert.assertNotNull(outputMessage);
        Assert.assertEquals(MessageType.TRANSACTION, outputMessage.getMessageType());
        Assert.assertEquals(transaction, ((TransactionMessage)outputMessage).getTransaction());
    }

    @Test
    public void processStatusMessageAndStartSync() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        PeerProcessor peerProcessor = new PeerProcessor();
        OutputProcessor outputProcessor = new OutputProcessor();

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        Peer peer = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.connectToPeer(peer, channel);

        Message message = new StatusMessage(new Status(peer.getId(), 1, 10));

        processor.processMessage(message, peer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(peer.getId()));

        Assert.assertEquals(11, channel.getMessages().size());

        for (int k = 0; k < 11; k++) {
            Message msg = channel.getMessages().get(k);

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
        OutputProcessor outputProcessor = new OutputProcessor();

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        Peer peer = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.connectToPeer(peer, channel);

        Message message = new StatusMessage(new Status(peer.getId(), 1, 10));

        processor.processMessage(message, peer);
        processor.processMessage(message, peer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(peer.getId()));

        Assert.assertEquals(11, channel.getMessages().size());

        for (int k = 0; k < 11; k++) {
            Message msg = channel.getMessages().get(k);

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
        OutputProcessor outputProcessor = new OutputProcessor();

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        Peer peer = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.connectToPeer(peer, channel);

        Message message1 = new StatusMessage(new Status(peer.getId(), 1, 5));
        Message message2 = new StatusMessage(new Status(peer.getId(), 1, 10));

        processor.processMessage(message1, peer);
        processor.processMessage(message2, peer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(peer.getId()));

        Assert.assertEquals(11, channel.getMessages().size());

        for (int k = 0; k < 11; k++) {
            Message msg = channel.getMessages().get(k);

            Assert.assertNotNull(msg);
            Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER, msg.getMessageType());

            GetBlockByNumberMessage gmsg = (GetBlockByNumberMessage)msg;

            Assert.assertEquals(k, gmsg.getNumber());
        }
    }
}
