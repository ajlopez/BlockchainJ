package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.*;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.KeyValueStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.simples.SimpleMessageChannel;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class MessageProcessorTest {
    @Test
    public void processBlockMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

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

        Peer sender = FactoryHelper.createRandomPeer();
        SendProcessor outputProcessor = new SendProcessor(sender);
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(sender, channel);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Message message = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(message, null);

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());

        expectedMessage(channel, sender, message);
    }

    @Test
    public void processOrphanBlockMessageAndSendGetBlockByHashToSender() throws IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor(blockChain);

        Peer sender = FactoryHelper.createRandomPeer();
        SendProcessor outputProcessor = new SendProcessor(sender);
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(sender, channel);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block block1 = new Block(1, blockChain.getBestBlock().getHash(), Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block2 = new Block(2, block1.getHash(), Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Message message = new BlockMessage(block2);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(message, sender);

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getNumber());

        expectedMessage(channel, sender, new GetBlockByHashMessage(block1.getHash()));
    }

    @Test
    public void processBlockMessageAndRelayBlockToOtherPeers() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Peer sender = FactoryHelper.createRandomPeer();
        SendProcessor sendProcessor = new SendProcessor(sender);

        Peer peer1 = FactoryHelper.createRandomPeer();
        SimpleMessageChannel channel1 = new SimpleMessageChannel();
        sendProcessor.connectToPeer(peer1, channel1);

        Peer peer2 = FactoryHelper.createRandomPeer();
        SimpleMessageChannel channel2 = new SimpleMessageChannel();
        sendProcessor.connectToPeer(peer2, channel2);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
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
        Peer originalSender = FactoryHelper.createRandomPeer();
        Peer sender = FactoryHelper.createRandomPeer();
        SendProcessor sendProcessor = new SendProcessor(sender);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
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
        SendProcessor outputProcessor = new SendProcessor(FactoryHelper.createRandomPeer());
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        Message message = new GetBlockByHashMessage(block.getHash());

        Peer sender = FactoryHelper.createRandomPeer();
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(sender, channel);

        processor.processMessage(message, sender);

        Message result = channel.getLastMessage();

        Assert.assertNull(result);
    }

    @Test
    public void processGetBlockByNumberMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        Peer sender = FactoryHelper.createRandomPeer();
        SendProcessor outputProcessor = new SendProcessor(sender);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Message blockMessage = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(blockMessage, null);

        Message getBlockMessage = new GetBlockByNumberMessage(block.getNumber());
        SimpleMessageChannel channel = new SimpleMessageChannel();
        Peer originalSender = FactoryHelper.createRandomPeer();
        outputProcessor.connectToPeer(originalSender, channel);

        processor.processMessage(getBlockMessage, originalSender);

        expectedMessage(channel, sender, blockMessage);
    }

    @Test
    public void processGetUnknownBlockByNumberMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        SendProcessor outputProcessor = new SendProcessor(FactoryHelper.createRandomPeer());
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        Message getBlockMessage = new GetBlockByNumberMessage(block.getNumber());
        Peer sender = FactoryHelper.createRandomPeer();
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

        Peer sender = FactoryHelper.createRandomPeer();
        SendProcessor outputProcessor = new SendProcessor(sender);
        SimpleMessageChannel channel = new SimpleMessageChannel();
        Peer receiver = FactoryHelper.createRandomPeer();
        outputProcessor.connectToPeer(receiver, channel);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(transactionProcessor, outputProcessor);

        processor.processMessage(message, FactoryHelper.createRandomPeer());

        List<Transaction> transactions = pool.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(transaction, transactions.get(0));

        expectedMessage(channel, sender, message);
    }

    @Test
    public void processStatusMessageAndStartSync() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        PeerProcessor peerProcessor = new PeerProcessor(1);
        SendProcessor outputProcessor = new SendProcessor(FactoryHelper.createRandomPeer());

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        Peer receiver = FactoryHelper.createRandomPeer();
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(receiver, channel);

        Message message = new StatusMessage(new Status(receiver.getId(), 1, 10, FactoryHelper.createRandomBlockHash()));

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
        PeerProcessor peerProcessor = new PeerProcessor(1);
        Peer firstPeer = FactoryHelper.createRandomPeer();
        SendProcessor outputProcessor = new SendProcessor(firstPeer);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        Peer secondPeer = FactoryHelper.createRandomPeer();
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(secondPeer, channel);

        Message message = new StatusMessage(new Status(secondPeer.getId(), 1, 10, FactoryHelper.createRandomBlockHash()));

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
        PeerProcessor peerProcessor = new PeerProcessor(1);
        Peer firstPeer = FactoryHelper.createRandomPeer();
        SendProcessor outputProcessor = new SendProcessor(firstPeer);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        Peer secondPeer = FactoryHelper.createRandomPeer();
        SimpleMessageChannel channel = new SimpleMessageChannel();
        outputProcessor.connectToPeer(secondPeer, channel);

        Message message1 = new StatusMessage(new Status(secondPeer.getId(), 1, 5, FactoryHelper.createRandomBlockHash()));
        Message message2 = new StatusMessage(new Status(secondPeer.getId(), 1, 10, FactoryHelper.createRandomBlockHash()));

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

    @Test
    public void processNodeTrieMessage() throws IOException {
        KeyValueStore keyValueStore0 = new HashMapStore();
        TrieStore trieStore0 = new TrieStore(keyValueStore0);

        Block block = FactoryHelper.createBlockChain(trieStore0, 1, 10).getBlockByNumber(1);
        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor warpProcessor = new WarpProcessor(accountStore);
        warpProcessor.processBlock(block);

        MessageProcessor processor = new MessageProcessor(null, null, null, null, warpProcessor);

        TrieNodeMessage message = new TrieNodeMessage(block.getStateRootHash(), TrieType.ACCOUNT, trieStore0.retrieve(block.getStateRootHash()).getEncoded());

        processor.processMessage(message, null);

        Set<Hash> result = warpProcessor.getPendingAccountHashes(block.getStateRootHash());

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(accountStore.exists(block.getStateRootHash()));
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
