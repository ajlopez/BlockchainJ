package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.merkle.MerkleTree;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.peers.PeerNode;
import com.ajlopez.blockchain.net.peers.TcpPeerClient;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.KeyValueStores;
import com.ajlopez.blockchain.store.MemoryKeyValueStores;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class NodeRunnerTest {
    @Test
    public void mineBlockUsingOneRunner() throws InterruptedException, IOException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        FactoryHelper.createBlockChainWithGenesis(stores);

        Semaphore semaphore = new Semaphore(0, true);

        Address coinbase = FactoryHelper.createRandomAddress();

        NodeRunner runner = new NodeRunner(true, 0, Collections.emptyList(), coinbase, new NetworkConfiguration((short)42), keyValueStores);

        runner.onNewBlock(blk -> {
            semaphore.release();
        });

        runner.start();

        semaphore.acquire();

        runner.stop();

        Block bestBlock = new BlockChain(stores).getBestBlockInformation().getBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertTrue(bestBlock.getNumber() > 0);
    }

    @Test
    public void processBlockInServerRunner() throws InterruptedException, IOException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores);

        Semaphore semaphore = new Semaphore(0, true);

        Address coinbase = FactoryHelper.createRandomAddress();

        NodeRunner runner = new NodeRunner(false, 3000, Collections.emptyList(), coinbase, new NetworkConfiguration((short)42), keyValueStores);

        runner.onNewBlock(blk -> {
            semaphore.release();
        });

        runner.start();

        Block block = new Block(1, blockChain.getBestBlockInformation().getBlockHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null);

        Message message = new BlockMessage(block);

        TcpPeerClient tcpPeerClient = new TcpPeerClient("127.0.0.1", 3000, (short)1, null);

        PeerNode peerNode = tcpPeerClient.connect();

        peerNode.postMessage(peerNode.getPeer(), message);

        semaphore.acquire();

        runner.stop();

        Block bestBlock = new BlockChain(stores).getBestBlockInformation().getBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertEquals(1, bestBlock.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock.getHash());
    }

    @Test
    public void connectTwoNodeRunners() throws InterruptedException, IOException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);
        FactoryHelper.createBlockChainWithGenesis(stores);
        KeyValueStores keyValueStores2 = new MemoryKeyValueStores();
        Stores stores2 = new Stores(keyValueStores2);

        Semaphore semaphore = new Semaphore(0, true);

        Address coinbase = FactoryHelper.createRandomAddress();

        NodeRunner runner1 = new NodeRunner(true, 3001, null, coinbase, new NetworkConfiguration((short)42), keyValueStores);
        NodeRunner runner2 = new NodeRunner(false, 0, Collections.singletonList("localhost:3001"), coinbase, new NetworkConfiguration((short)42), keyValueStores2);

        runner2.onNewBlock(blk -> {
            if (blk.getNumber() > 0)
                semaphore.release();
        });

        runner1.start();
        runner2.start();

        semaphore.acquire();

        runner2.stop();
        runner1.stop();

        Block bestBlock = new BlockChain(stores2).getBestBlockInformation().getBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertTrue(bestBlock.getNumber() > 0);
    }
}
