package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.*;
import com.ajlopez.blockchain.net.peers.PeerConnection;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.test.utils.NodesHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 14/10/2018.
 */
public class NodeProcessorTest {
    @Test
    public void createWithPeer() {
        BlockChain blockChain = new BlockChain();
        Peer peer = FactoryHelper.createRandomPeer();
        Address coinbase = FactoryHelper.createRandomAddress();

        NodeProcessor nodeProcessor = new NodeProcessor(new NetworkConfiguration((short)42), peer, blockChain, null, coinbase);

        Assert.assertSame(peer, nodeProcessor.getPeer());
    }

    @Test
    public void getStatus() {
        NetworkConfiguration networkConfiguration = new NetworkConfiguration((short)42);
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        Peer peer = FactoryHelper.createRandomPeer();
        Address coinbase = FactoryHelper.createRandomAddress();

        NodeProcessor nodeProcessor = new NodeProcessor(networkConfiguration, peer, blockChain, null, coinbase);

        Status result = nodeProcessor.getStatus();

        Assert.assertNotNull(result);
        Assert.assertEquals(blockChain.getBestBlockNumber(), result.getBestBlockNumber());
        Assert.assertEquals(blockChain.getBestBlock().getHash(), result.getBestBlockHash());
        Assert.assertEquals(networkConfiguration.getNetworkNumber(), result.getNetworkNumber());
        Assert.assertEquals(peer.getId(), result.getPeerId());
    }

    @Test
    public void processBlockMessage() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000, coinbase);
        Message message = new BlockMessage(block);

        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message);

        NodesHelper.runNodeProcessors(nodeProcessor);

        Block result = blockChain.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void mineBlock() throws InterruptedException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);

        Semaphore semaphore = new Semaphore(0, true);

        blockChain.onBlock(blk -> {
            semaphore.release();
        });

        nodeProcessor.startMessagingProcess();
        nodeProcessor.startMiningProcess();

        semaphore.acquire();

        nodeProcessor.stopMiningProcess();
        nodeProcessor.stopMessagingProcess();

        Block block1 = blockChain.getBlockByNumber(1);

        Assert.assertNotNull(block1);
        Assert.assertEquals(1, block1.getNumber());
    }

    @Test
    public void processTenRepeatedBlockMessages() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000, coinbase);

        Message message = new BlockMessage(block);

        for (int k = 0; k < 10; k++)
            nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message);

        NodesHelper.runNodeProcessors(nodeProcessor);

        Block result = blockChain.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void processTwoConsecutiveBlockMessages() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO);
        Block block1 = new Block(1, genesis.getHash(), Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase);

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message0);
        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message1);

        NodesHelper.runNodeProcessors(nodeProcessor);

        Block result = blockChain.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block1.getHash(), result.getHash());
    }

    @Test
    public void processTenBlockMessages() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);
        List<Block> blocks = FactoryHelper.createBlocks(9);

        for (Block block: blocks) {
            Message message = new BlockMessage(block);
            nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message);
        }

        NodesHelper.runNodeProcessors(nodeProcessor);

        Block result = blockChain.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(9, result.getNumber());
        Assert.assertEquals(blocks.get(9).getHash(), result.getHash());
    }

    @Test
    public void processTwoConsecutiveBlockMessagesOutOfOrder() throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO);
        Block block1 = new Block(1, genesis.getHash(), Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase);

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message1);
        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message0);

        NodesHelper.runNodeProcessors(nodeProcessor);

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

        nodeProcessor1.connectTo(nodeProcessor2);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO);
        Block block1 = new Block(1, genesis.getHash(), Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase);

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        nodeProcessor1.postMessage(null, message0);
        nodeProcessor1.postMessage(null, message1);

        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);

        Block result1 = blockChain1.getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(block1.getHash(), result1.getHash());

        Block result2 = blockChain2.getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(block1.getHash(), result2.getHash());
    }

    @Test
    public void processTwoBlockMessagesUsingTwoNodesConnectedByPipes() throws InterruptedException, IOException {
        BlockChain blockChain1 = new BlockChain();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);
        BlockChain blockChain2 = new BlockChain();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);

        List<PeerConnection> connections = NodesHelper.connectNodeProcessors(nodeProcessor1, nodeProcessor2);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO);
        Block block1 = new Block(1, genesis.getHash(), Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase);

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        nodeProcessor1.postMessage(null, message0);
        nodeProcessor1.postMessage(null, message1);

        connections.forEach(connection -> connection.start());
        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);
        connections.forEach(connection -> connection.stop());

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

        nodeProcessor1.connectTo(nodeProcessor2);
        nodeProcessor2.connectTo(nodeProcessor1);

        for (Block block : blocks)
            Assert.assertTrue(blockChain1.connectBlock(block));

        for (int k = 0; k < 10; k++)
            Assert.assertNotNull(blockChain1.getBlockByNumber(k));

        Status status = new Status(nodeProcessor1.getPeer().getId(), 42,9, FactoryHelper.createRandomBlockHash());
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);

        Block result1 = blockChain1.getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = blockChain2.getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());
    }

    @Test
    public void synchronizeTwoNodesCheckingOnBlock() throws InterruptedException {
        List<Block> blocks = FactoryHelper.createBlocks(9);
        Block bestBlock = blocks.get(9);

        BlockChain blockChain1 = new BlockChain();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);
        BlockChain blockChain2 = new BlockChain();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);

        nodeProcessor1.connectTo(nodeProcessor2);
        nodeProcessor2.connectTo(nodeProcessor1);

        Semaphore semaphore = new Semaphore(0, true);

        blockChain2.onBlock(blk -> {
            if (blk.getNumber() == bestBlock.getNumber())
                semaphore.release();
        });

        for (Block block : blocks)
            Assert.assertTrue(blockChain1.connectBlock(block));

        for (int k = 0; k < 10; k++)
            Assert.assertNotNull(blockChain1.getBlockByNumber(k));

        Status status = new Status(nodeProcessor1.getPeer().getId(), 42,9, FactoryHelper.createRandomBlockHash());
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);

        semaphore.acquire();

        Block result1 = blockChain1.getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = blockChain2.getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());
    }

    @Test
    public void synchronizeTwoNodesConnectedByPipes() throws InterruptedException, IOException {
        List<Block> blocks = FactoryHelper.createBlocks(9);
        Block bestBlock = blocks.get(9);

        BlockChain blockChain1 = new BlockChain();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);
        BlockChain blockChain2 = new BlockChain();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);

        List<PeerConnection> connections = NodesHelper.connectNodeProcessors(nodeProcessor1, nodeProcessor2);

        for (Block block : blocks)
            Assert.assertTrue(blockChain1.connectBlock(block));

        for (int k = 0; k < 10; k++)
            Assert.assertNotNull(blockChain1.getBlockByNumber(k));

        Status status = new Status(nodeProcessor1.getPeer().getId(), 42,9, FactoryHelper.createRandomBlockHash());
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        connections.forEach(connection -> connection.start());
        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);
        connections.forEach(connection -> connection.stop());

        Block result1 = blockChain1.getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = blockChain2.getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());
    }

    @Test
    public void synchronizeThreeNodes() throws InterruptedException {
        List<Block> blocks = FactoryHelper.createBlocks(9);
        Block bestBlock = blocks.get(9);

        BlockChain blockChain1 = new BlockChain();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);
        BlockChain blockChain2 = new BlockChain();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);
        BlockChain blockChain3 = new BlockChain();
        NodeProcessor nodeProcessor3 = FactoryHelper.createNodeProcessor(blockChain3);

        nodeProcessor1.connectTo(nodeProcessor2);
        nodeProcessor2.connectTo(nodeProcessor1);
        nodeProcessor2.connectTo(nodeProcessor3);

        for (Block block : blocks)
            Assert.assertTrue(blockChain1.connectBlock(block));

        for (int k = 0; k < 10; k++)
            Assert.assertNotNull(blockChain1.getBlockByNumber(k));

        Status status = new Status(nodeProcessor1.getPeer().getId(), 42,9, FactoryHelper.createRandomBlockHash());
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2, nodeProcessor3);

        Block result1 = blockChain1.getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = blockChain2.getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());

        Block result3 = blockChain3.getBestBlock();

        Assert.assertNotNull(result3);
        Assert.assertEquals(bestBlock.getHash(), result3.getHash());
    }

    @Test
    public void synchronizeThreeNodesConnectedByPipes() throws InterruptedException, IOException {
        List<Block> blocks = FactoryHelper.createBlocks(9);
        Block bestBlock = blocks.get(9);

        BlockChain blockChain1 = new BlockChain();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);
        BlockChain blockChain2 = new BlockChain();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);
        BlockChain blockChain3 = new BlockChain();
        NodeProcessor nodeProcessor3 = FactoryHelper.createNodeProcessor(blockChain3);

        List<PeerConnection> connections = NodesHelper.connectNodeProcessors(nodeProcessor1, nodeProcessor2, nodeProcessor3);

        for (Block block : blocks)
            Assert.assertTrue(blockChain1.connectBlock(block));

        for (int k = 0; k < 10; k++)
            Assert.assertNotNull(blockChain1.getBlockByNumber(k));

        Status status = new Status(nodeProcessor1.getPeer().getId(), 42,9, FactoryHelper.createRandomBlockHash());
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        connections.forEach(connection -> connection.start());
        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2, nodeProcessor3);
        connections.forEach(connection -> connection.stop());

        Block result1 = blockChain1.getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = blockChain2.getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());

        Block result3 = blockChain3.getBestBlock();

        Assert.assertNotNull(result3);
        Assert.assertEquals(bestBlock.getHash(), result3.getHash());
    }

    @Test
    public void processTransactionMessage() throws InterruptedException {
        Transaction transaction = FactoryHelper.createTransaction(100);
        Message message = new TransactionMessage(transaction);

        BlockChain blockChain = new BlockChain();
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(blockChain);

        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message);

        NodesHelper.runNodeProcessors(nodeProcessor);

        List<Transaction> transactions = nodeProcessor.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());

        Transaction result = transactions.get(0);

        Assert.assertNotNull(result);
        Assert.assertEquals(transaction.getHash(), result.getHash());
    }

    @Test
    public void processTransactionMessageWithRelayToOtherNode() throws InterruptedException {
        Transaction transaction = FactoryHelper.createTransaction(100);
        Message message = new TransactionMessage(transaction);

        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor();

        nodeProcessor1.connectTo(nodeProcessor2);

        nodeProcessor1.postMessage(null, message);

        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);

        List<Transaction> transactions1 = nodeProcessor1.getTransactions();

        Assert.assertNotNull(transactions1);
        Assert.assertFalse(transactions1.isEmpty());
        Assert.assertEquals(1, transactions1.size());

        Transaction result1 = transactions1.get(0);

        Assert.assertNotNull(result1);
        Assert.assertEquals(transaction.getHash(), result1.getHash());

        List<Transaction> transactions2 = nodeProcessor2.getTransactions();

        Assert.assertNotNull(transactions2);
        Assert.assertFalse(transactions2.isEmpty());
        Assert.assertEquals(1, transactions2.size());

        Transaction result2 = transactions2.get(0);

        Assert.assertNotNull(result2);
        Assert.assertEquals(transaction.getHash(), result2.getHash());
    }
}
