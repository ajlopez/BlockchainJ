package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.execution.BlockExecutionResult;
import com.ajlopez.blockchain.execution.BlockExecutor;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.*;
import com.ajlopez.blockchain.net.peers.PeerConnection;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.test.utils.NodesHelper;
import com.ajlopez.blockchain.vms.eth.TrieStorageProvider;
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
    public void createWithPeer() throws IOException {
        Peer peer = FactoryHelper.createRandomPeer();
        Address coinbase = FactoryHelper.createRandomAddress();

        NodeProcessor nodeProcessor = new NodeProcessor(new NetworkConfiguration((short)42), peer, new MemoryKeyValueStores(), coinbase);

        Assert.assertSame(peer, nodeProcessor.getPeer());
    }

    @Test
    public void getStatus() throws IOException {
        NetworkConfiguration networkConfiguration = new NetworkConfiguration((short)42);
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores);
        Peer peer = FactoryHelper.createRandomPeer();
        Address coinbase = FactoryHelper.createRandomAddress();

        NodeProcessor nodeProcessor = new NodeProcessor(networkConfiguration, peer, keyValueStores, coinbase);

        Status result = nodeProcessor.getStatus();

        Assert.assertNotNull(result);
        Assert.assertEquals(blockChain.getBestBlockNumber(), result.getBestBlockNumber());
        Assert.assertEquals(blockChain.getBestBlock().getHash(), result.getBestBlockHash());
        Assert.assertEquals(blockChain.getBestBlockTotalDifficulty(), result.getBestTotalDifficulty());
        Assert.assertEquals(networkConfiguration.getNetworkNumber(), result.getNetworkNumber());
        Assert.assertEquals(peer.getId(), result.getPeerId());
    }

    @Test
    public void processBlockMessage() throws InterruptedException, IOException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores);
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(keyValueStores);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1, blockChain.getBestBlock().getHash(), null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Message message = new BlockMessage(block);

        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message);

        NodesHelper.runNodeProcessors(nodeProcessor);

        Block result = nodeProcessor.getBlockChain().getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getNumber());
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void mineBlock() throws InterruptedException, IOException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        FactoryHelper.createBlockChainWithGenesis(stores);
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(keyValueStores);

        Semaphore semaphore = new Semaphore(0, true);

        nodeProcessor.onNewBestBlock(blk -> {
            semaphore.release();
        });

        nodeProcessor.startMessagingProcess();
        nodeProcessor.startMiningProcess();

        semaphore.acquire();

        nodeProcessor.stopMiningProcess();
        nodeProcessor.stopMessagingProcess();

        Block block1 = nodeProcessor.getBlockChain().getBestBlock();

        Assert.assertNotNull(block1);
        Assert.assertEquals(1, block1.getNumber());
    }

    @Test
    public void processTenRepeatedBlockMessages() throws InterruptedException, IOException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores);
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(keyValueStores);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1, blockChain.getBlockByNumber(0).getHash(), null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Message message = new BlockMessage(block);

        for (int k = 0; k < 10; k++)
            nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message);

        NodesHelper.runNodeProcessors(nodeProcessor);

        Block result = nodeProcessor.getBlockChain().getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void processTwoConsecutiveBlockMessages() throws InterruptedException, IOException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores);
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(keyValueStores);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block1 = new Block(1, blockChain.getBlockByNumber(0).getHash(), null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block2 = new Block(2, block1.getHash(), null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Message message1 = new BlockMessage(block1);
        Message message2 = new BlockMessage(block2);

        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message1);
        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message2);

        NodesHelper.runNodeProcessors(nodeProcessor);

        Block result = nodeProcessor.getBlockChain().getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block2.getHash(), result.getHash());
    }

    @Test
    public void processTenBlockMessages() throws InterruptedException, IOException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        FactoryHelper.createBlockChainWithGenesis(stores);
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(keyValueStores);
        List<Block> blocks = FactoryHelper.createBlocks(9);

        for (Block block: blocks) {
            Message message = new BlockMessage(block);
            nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message);
        }

        NodesHelper.runNodeProcessors(nodeProcessor);

        Block result = nodeProcessor.getBlockChain().getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(9, result.getNumber());
        Assert.assertEquals(blocks.get(9).getHash(), result.getHash());
    }

    @Test
    public void processTwoConsecutiveBlockMessagesOutOfOrder() throws InterruptedException, IOException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores);
        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(keyValueStores);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block1 = new Block(1, blockChain.getBlockByNumber(0).getHash(), null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block2 = new Block(2, block1.getHash(), null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Message message1 = new BlockMessage(block1);
        Message message2 = new BlockMessage(block2);

        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message2);
        nodeProcessor.postMessage(FactoryHelper.createRandomPeer(), message1);

        NodesHelper.runNodeProcessors(nodeProcessor);

        Block result = nodeProcessor.getBlockChain().getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block2.getHash(), result.getHash());
    }

    @Test
    public void processTwoBlockMessagesUsingTwoNodes() throws InterruptedException, IOException {
        KeyValueStores keyValueStores1 = new MemoryKeyValueStores();
        Stores stores1 = new Stores(keyValueStores1);
        KeyValueStores keyValueStores2 = new MemoryKeyValueStores();
        Stores stores2 = new Stores(keyValueStores2);

        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores1);
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);

        nodeProcessor1.connectTo(nodeProcessor2);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block1 = new Block(1, genesis.getHash(), null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        nodeProcessor1.postMessage(null, message0);
        nodeProcessor1.postMessage(null, message1);

        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);

        Block result1 = nodeProcessor1.getBlockChain().getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(block1.getHash(), result1.getHash());

        Block result2 = nodeProcessor2.getBlockChain().getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(block1.getHash(), result2.getHash());
    }

    @Test
    public void processTwoBlockMessagesUsingTwoNodesConnectedByPipes() throws InterruptedException, IOException {
        KeyValueStores keyValueStores1 = new MemoryKeyValueStores();
        KeyValueStores keyValueStores2 = new MemoryKeyValueStores();

        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores1);
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);

        List<PeerConnection> connections = NodesHelper.connectNodeProcessors(nodeProcessor1, nodeProcessor2);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block1 = new Block(1, genesis.getHash(), null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Message message0 = new BlockMessage(genesis);
        Message message1 = new BlockMessage(block1);

        nodeProcessor1.postMessage(null, message0);
        nodeProcessor1.postMessage(null, message1);

        connections.forEach(connection -> connection.start());
        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);
        connections.forEach(connection -> connection.stop());

        Block result1 = nodeProcessor1.getBlockChain().getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(block1.getHash(), result1.getHash());

        Block result2 = nodeProcessor2.getBlockChain().getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(block1.getHash(), result2.getHash());
    }

    @Test
    public void synchronizeTwoNodes() throws InterruptedException, IOException {
        KeyValueStores keyValueStores1 = new MemoryKeyValueStores();
        Stores stores1 = new Stores(keyValueStores1);
        KeyValueStores keyValueStores2 = new MemoryKeyValueStores();

        BlockChain blockChain1 = FactoryHelper.createBlockChain(stores1, 300, 0);
        Block bestBlock = blockChain1.getBestBlock();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores1);

        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);

        nodeProcessor1.connectTo(nodeProcessor2);
        nodeProcessor2.connectTo(nodeProcessor1);

        Status status = new Status(nodeProcessor1.getPeer().getId(), 42,blockChain1.getBestBlockNumber(), blockChain1.getBestBlock().getHash(), null);
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        NodesHelper.runNodeProcessors(bestBlock, nodeProcessor1, nodeProcessor2);

        Block result1 = nodeProcessor1.getBlockChain().getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = nodeProcessor2.getBlockChain().getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());
    }

    @Test
    public void synchronizeTwoNodesWithTransactions() throws InterruptedException, IOException {
        MemoryKeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores1 = new Stores(keyValueStores);
        BlockChain blockChain1 = FactoryHelper.createBlockChain(stores1,300, 10);
        Block bestBlock = blockChain1.getBestBlock();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores);

        MemoryKeyValueStores keyValueStores2 = new MemoryKeyValueStores();
        Stores stores2 = new Stores(keyValueStores2);

        Assert.assertNotNull(stores1.getAccountTrieStore().retrieve(blockChain1.getBlockByNumber(0).getStateRootHash()));

        Trie genesisTrie = stores1.getAccountTrieStore().retrieve(blockChain1.getBlockByNumber(0).getStateRootHash());
        genesisTrie.saveToStore(stores2.getAccountTrieStore());

        Assert.assertNotNull(stores2.getAccountTrieStore().retrieve(blockChain1.getBlockByNumber(0).getStateRootHash()));

        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);

        nodeProcessor1.connectTo(nodeProcessor2);
        nodeProcessor2.connectTo(nodeProcessor1);

        Status status = new Status(nodeProcessor1.getPeer().getId(), 42,blockChain1.getBestBlockNumber(), blockChain1.getBestBlock().getHash(), null);
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        NodesHelper.runNodeProcessors(blockChain1.getBestBlock(), nodeProcessor1, nodeProcessor2);

        Block result1 =nodeProcessor1.getBlockChain().getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = nodeProcessor2.getBlockChain().getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());
    }

    @Test
    public void executeBlockUsingTwoNodesBeamSynchronization() throws IOException {
        MemoryKeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain1 = FactoryHelper.createBlockChain(stores,300, 10);
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores);

        MemoryKeyValueStores keyValueStores2 = new MemoryKeyValueStores();

        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);

        nodeProcessor1.connectTo(nodeProcessor2);
        nodeProcessor2.connectTo(nodeProcessor1);

        SendProcessor sendProcessor = nodeProcessor2.getSendProcessor();
        KeyValueProcessor keyValueProcessor = nodeProcessor2.getKeyValueProcessor();

        KeyValueStore remoteCodeKeyValueStore = new RemoteKeyValueStore(KeyValueStoreType.CODES, sendProcessor, keyValueProcessor);
        KeyValueStore remoteAccountKeyValueStore = new RemoteKeyValueStore(KeyValueStoreType.ACCOUNTS, sendProcessor, keyValueProcessor);
        KeyValueStore remoteStorageKeyValueStore = new RemoteKeyValueStore(KeyValueStoreType.STORAGE, sendProcessor, keyValueProcessor);

        CodeStore codeStore = new CodeStore(new DualKeyValueStore(remoteCodeKeyValueStore, keyValueStores2.getCodeKeyValueStore()));
        TrieStore accountTrieStore = new TrieStore(new DualKeyValueStore(remoteAccountKeyValueStore, keyValueStores2.getAccountKeyValueStore()));
        TrieStore storageTrieStore = new TrieStore(new DualKeyValueStore(remoteStorageKeyValueStore, keyValueStores2.getStorageKeyValueStore()));

        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(accountTrieStore);
        TrieStorageProvider trieStorageProvider = new TrieStorageProvider(storageTrieStore);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, trieStorageProvider, codeStore);
        Block bestBlock = blockChain1.getBestBlock();

        nodeProcessor1.startMessagingProcess();
        nodeProcessor2.startMessagingProcess();

        BlockExecutionResult result = blockExecutor.executeBlock(bestBlock, blockChain1.getBlockByNumber(bestBlock.getNumber() - 1).getStateRootHash());

        nodeProcessor2.stopMessagingProcess();
        nodeProcessor1.stopMessagingProcess();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStateRootHash());
        Assert.assertEquals(bestBlock.getStateRootHash(), result.getStateRootHash());
    }

    @Test
    public void synchronizeTwoNodesConnectedByPipes() throws InterruptedException, IOException {
        MemoryKeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain1 = FactoryHelper.createBlockChain(stores,300, 0);
        Block bestBlock = blockChain1.getBestBlock();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores);

        MemoryKeyValueStores keyValueStores2 = new MemoryKeyValueStores();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);

        List<PeerConnection> connections = NodesHelper.connectNodeProcessors(nodeProcessor1, nodeProcessor2);

        Status status = new Status(nodeProcessor1.getPeer().getId(), 42,blockChain1.getBestBlockNumber(), blockChain1.getBestBlock().getHash(), null);
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        connections.forEach(connection -> connection.start());
        NodesHelper.runNodeProcessors(blockChain1.getBestBlock(), nodeProcessor1, nodeProcessor2);
        connections.forEach(connection -> connection.stop());

        Block result1 = nodeProcessor1.getBlockChain().getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getNumber(), result1.getNumber());
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = nodeProcessor2.getBlockChain().getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getNumber(), result2.getNumber());
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());
    }

    @Test
    public void synchronizeThreeNodes() throws InterruptedException, IOException {
        List<Block> blocks = FactoryHelper.createBlocks(9);
        Block bestBlock = blocks.get(9);

        MemoryKeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain1 = new BlockChain(stores);

        for (Block block : blocks)
            Assert.assertTrue(blockChain1.connectBlock(block));

        for (int k = 0; k < 10; k++)
            Assert.assertNotNull(blockChain1.getBlockByNumber(k));

        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores);
        MemoryKeyValueStores keyValueStores2 = new MemoryKeyValueStores();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);
        MemoryKeyValueStores keyValueStores3 = new MemoryKeyValueStores();
        NodeProcessor nodeProcessor3 = FactoryHelper.createNodeProcessor(keyValueStores3);

        nodeProcessor1.connectTo(nodeProcessor2);
        nodeProcessor2.connectTo(nodeProcessor1);
        nodeProcessor2.connectTo(nodeProcessor3);

        Status status = new Status(nodeProcessor1.getPeer().getId(), 42,9, FactoryHelper.createRandomBlockHash(), null);
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        NodesHelper.runNodeProcessors(blockChain1.getBestBlock(), nodeProcessor1, nodeProcessor2, nodeProcessor3);

        Block result1 = nodeProcessor1.getBlockChain().getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = nodeProcessor2.getBlockChain().getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());

        Block result3 = nodeProcessor3.getBlockChain().getBestBlock();

        Assert.assertNotNull(result3);
        Assert.assertEquals(bestBlock.getHash(), result3.getHash());
    }

    @Test
    public void synchronizeThreeNodesConnectedByPipes() throws InterruptedException, IOException {
        List<Block> blocks = FactoryHelper.createBlocks(9);
        Block bestBlock = blocks.get(9);

        MemoryKeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain1 = new BlockChain(stores);

        for (Block block : blocks)
            Assert.assertTrue(blockChain1.connectBlock(block));

        for (int k = 0; k < 10; k++)
            Assert.assertNotNull(blockChain1.getBlockByNumber(k));

        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores);
        MemoryKeyValueStores keyValueStores2 = new MemoryKeyValueStores();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);
        MemoryKeyValueStores keyValueStores3 = new MemoryKeyValueStores();
        NodeProcessor nodeProcessor3 = FactoryHelper.createNodeProcessor(keyValueStores3);

        List<PeerConnection> connections = NodesHelper.connectNodeProcessors(nodeProcessor1, nodeProcessor2, nodeProcessor3);

        Status status = new Status(nodeProcessor1.getPeer().getId(), 42,9, FactoryHelper.createRandomBlockHash(), null);
        StatusMessage statusMessage = new StatusMessage(status);

        nodeProcessor2.postMessage(nodeProcessor1.getPeer(), statusMessage);

        connections.forEach(connection -> connection.start());
        NodesHelper.runNodeProcessors(blockChain1.getBestBlock(), nodeProcessor1, nodeProcessor2, nodeProcessor3);
        connections.forEach(connection -> connection.stop());

        Block result1 = nodeProcessor1.getBlockChain().getBestBlock();

        Assert.assertNotNull(result1);
        Assert.assertEquals(bestBlock.getHash(), result1.getHash());

        Block result2 = nodeProcessor2.getBlockChain().getBestBlock();

        Assert.assertNotNull(result2);
        Assert.assertEquals(bestBlock.getHash(), result2.getHash());

        Block result3 = nodeProcessor3.getBlockChain().getBestBlock();

        Assert.assertNotNull(result3);
        Assert.assertEquals(bestBlock.getHash(), result3.getHash());
    }

    @Test
    public void processTransactionMessage() throws InterruptedException, IOException {
        Transaction transaction = FactoryHelper.createTransaction(100);
        Message message = new TransactionMessage(transaction);

        NodeProcessor nodeProcessor = FactoryHelper.createNodeProcessor(new MemoryKeyValueStores());

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
    public void processTransactionMessageWithRelayToOtherNode() throws InterruptedException, IOException {
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
