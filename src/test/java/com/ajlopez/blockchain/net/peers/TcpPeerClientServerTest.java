package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.merkle.MerkleTree;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.processors.NodeProcessor;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.KeyValueStores;
import com.ajlopez.blockchain.store.MemoryKeyValueStores;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.test.utils.NodesHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class TcpPeerClientServerTest {
    @Test
    public void connectClientServer() throws IOException, InterruptedException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain1 = FactoryHelper.createBlockChainWithGenesis(stores);
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores);

        KeyValueStores keyValueStores2 = new MemoryKeyValueStores();
        Stores stores2 = new Stores(keyValueStores2);
        FactoryHelper.createBlockChainWithGenesis(stores2);
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);

        Semaphore semaphore = new Semaphore(0, true);

        nodeProcessor2.onNewBlock(blk -> {
            semaphore.release();
        });

        TcpPeerServer server = new TcpPeerServer((short) 1, 4000, nodeProcessor2);
        server.start();

        TcpPeerClient client = new TcpPeerClient("localhost", 4000, (short)1, nodeProcessor1);
        client.connect();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1, blockChain1.getBestBlock().getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Message message = new BlockMessage(block);

        nodeProcessor1.postMessage(FactoryHelper.createRandomPeer(), message);

        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);

        semaphore.acquire();

        server.stop();

        Block bestBlock1 = new BlockChain(stores).getBestBlock();

        Assert.assertNotNull(bestBlock1);
        Assert.assertEquals(block.getNumber(), bestBlock1.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock1.getHash());

        Block bestBlock2 = new BlockChain(stores2).getBestBlock();

        Assert.assertNotNull(bestBlock2);
        Assert.assertEquals(block.getNumber(), bestBlock2.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock2.getHash());
    }

    @Test
    public void connectClientServerAndSynchronizeClient() throws IOException, InterruptedException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain1 = FactoryHelper.createBlockChainWithGenesis(stores);
        FactoryHelper.extendBlockChainWithBlocks(blockChain1, 10);

        Assert.assertEquals(10, blockChain1.getBestBlockNumber());

        Block block = blockChain1.getBestBlock();

        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores);
        KeyValueStores keyValueStores2 = new MemoryKeyValueStores();
        Stores stores2 = new Stores(keyValueStores2);
        FactoryHelper.createBlockChainWithGenesis(stores2);
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);

        Semaphore semaphore = new Semaphore(0, true);

        nodeProcessor2.onNewBlock(blk -> {
            if (blk.getNumber() == 10)
                semaphore.release();
        });

        TcpPeerServer server = new TcpPeerServer((short)1, 4001, nodeProcessor2);
        server.start();

        TcpPeerClient client = new TcpPeerClient("localhost", 4001, (short)1, nodeProcessor1);
        client.connect();

        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);

        semaphore.acquire();

        server.stop();

        Block bestBlock1 = new BlockChain(stores).getBestBlock();

        Assert.assertNotNull(bestBlock1);
        Assert.assertEquals(block.getNumber(), bestBlock1.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock1.getHash());

        Block bestBlock2 = new BlockChain(stores2).getBestBlock();

        Assert.assertNotNull(bestBlock2);
        Assert.assertEquals(block.getNumber(), bestBlock2.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock2.getHash());
    }

    @Test
    public void connectClientServerAndSynchronizeServer() throws IOException, InterruptedException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        FactoryHelper.createBlockChainWithGenesis(stores);

        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(keyValueStores);

        KeyValueStores keyValueStores2 = new MemoryKeyValueStores();
        Stores stores2 = new Stores(keyValueStores2);
        BlockChain blockChain2 = FactoryHelper.createBlockChainWithGenesis(stores2);
        FactoryHelper.extendBlockChainWithBlocks(blockChain2, 10);

        Assert.assertEquals(10, blockChain2.getBestBlockNumber());

        Block block = blockChain2.getBestBlock();

        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(keyValueStores2);

        Semaphore semaphore = new Semaphore(0, true);

        nodeProcessor1.onNewBlock(blk -> {
            if (blk.getNumber() == 10)
                semaphore.release();
        });

        TcpPeerServer server = new TcpPeerServer((short)1, 4002, nodeProcessor2);
        server.start();

        TcpPeerClient client = new TcpPeerClient("localhost", 4002, (short)1, nodeProcessor1);
        client.connect();

        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);

        semaphore.acquire();

        server.stop();

        Block bestBlock1 = new BlockChain(stores).getBestBlock();

        Assert.assertNotNull(bestBlock1);
        Assert.assertEquals(block.getNumber(), bestBlock1.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock1.getHash());

        Block bestBlock2 = new BlockChain(stores2).getBestBlock();

        Assert.assertNotNull(bestBlock2);
        Assert.assertEquals(block.getNumber(), bestBlock2.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock2.getHash());
    }
}
