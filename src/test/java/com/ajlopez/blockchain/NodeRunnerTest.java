package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.ObjectContext;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.config.NodeConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.merkle.MerkleTree;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.peers.PeerNode;
import com.ajlopez.blockchain.net.peers.TcpPeerClient;
import com.ajlopez.blockchain.config.MinerConfiguration;
import com.ajlopez.blockchain.processors.MinerProcessor;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.MemoryKeyValueStores;
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
        ObjectContext objectContext = new ObjectContext(new MemoryKeyValueStores());
        FactoryHelper.createBlockChainWithGenesis(objectContext);

        Semaphore semaphore = new Semaphore(0, true);

        Address coinbase = FactoryHelper.createRandomAddress();
        MinerProcessor minerProcessor = new MinerProcessor(objectContext.getBlockChain(), objectContext.getTransactionPool(), objectContext.getStores(), new MinerConfiguration(true, coinbase, 6_000_000L, 10));
        NodeRunner runner = new NodeRunner(new NodeConfiguration(0, Collections.emptyList()), new NetworkConfiguration((short)42), objectContext);

        minerProcessor.onMinedBlock(blk -> {
            runner.getNodeProcessor().postMessage(new BlockMessage(blk));
        });

        runner.getNodeProcessor().onNewBlock(blk -> {
            semaphore.release();
        });

        runner.start();
        minerProcessor.start();

        semaphore.acquire();

        minerProcessor.stop();
        runner.stop();

        Block bestBlock = objectContext.getBlockChain().getBestBlockInformation().getBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertTrue(bestBlock.getNumber() > 0);
    }

    @Test
    public void processBlockInServerRunner() throws InterruptedException, IOException {
        ObjectContext objectContext = new ObjectContext(new MemoryKeyValueStores());
        FactoryHelper.createBlockChainWithGenesis(objectContext);
        Semaphore semaphore = new Semaphore(0, true);

        Assert.assertNotNull(objectContext.getBlockChain());
        Assert.assertNotNull(objectContext.getBlockChain().getBestBlockInformation());

        Address coinbase = FactoryHelper.createRandomAddress();

        NodeRunner runner = new NodeRunner(new NodeConfiguration(3000, Collections.emptyList()), new NetworkConfiguration((short)42), objectContext);

        runner.getNodeProcessor().onNewBlock(blk -> {
            semaphore.release();
        });

        runner.start();

        Block block = new Block(1, objectContext.getBlockChain().getBestBlockInformation().getBlockHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        Message message = new BlockMessage(block);

        TcpPeerClient tcpPeerClient = new TcpPeerClient("127.0.0.1", 3000, (short)1, null);

        PeerNode peerNode = tcpPeerClient.connect();

        peerNode.postMessage(peerNode.getPeer(), message);

        semaphore.acquire();

        runner.stop();

        Block bestBlock = objectContext.getBlockChain().getBestBlockInformation().getBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertEquals(1, bestBlock.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock.getHash());
    }

    @Test
    public void connectTwoNodeRunners() throws InterruptedException, IOException {
        ObjectContext objectContext1 = new ObjectContext(new MemoryKeyValueStores());
        FactoryHelper.createBlockChainWithGenesis(objectContext1);
        ObjectContext objectContext2 = new ObjectContext(new MemoryKeyValueStores());

        Semaphore semaphore = new Semaphore(0, true);

        NodeRunner runner1 = new NodeRunner(new NodeConfiguration(3001, null), new NetworkConfiguration((short)42), objectContext1);
        NodeRunner runner2 = new NodeRunner(new NodeConfiguration(0, Collections.singletonList("localhost:3001")), new NetworkConfiguration((short)42), objectContext2);

        runner2.getNodeProcessor().onNewBlock(blk -> {
            semaphore.release();
        });

        runner1.start();
        runner2.start();

        semaphore.acquire();

        runner2.stop();
        runner1.stop();

        Block bestBlock = objectContext2.getBlockChain().getBestBlockInformation().getBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertEquals(0, bestBlock.getNumber());
    }

    @Test
    public void mineBlockConnectingTwoNodeRunners() throws InterruptedException, IOException {
        ObjectContext objectContext1 = new ObjectContext(new MemoryKeyValueStores());
        FactoryHelper.createBlockChainWithGenesis(objectContext1);
        ObjectContext objectContext2 = new ObjectContext(new MemoryKeyValueStores());

        Semaphore semaphore = new Semaphore(0, true);

        Address coinbase = FactoryHelper.createRandomAddress();
        MinerProcessor minerProcessor = new MinerProcessor(objectContext1.getBlockChain(), objectContext1.getTransactionPool(), objectContext1.getStores(), new MinerConfiguration(true, coinbase, 6_000_000L, 10));

        NodeRunner runner1 = new NodeRunner(new NodeConfiguration(3001, null), new NetworkConfiguration((short)42), objectContext1);
        NodeRunner runner2 = new NodeRunner(new NodeConfiguration(0, Collections.singletonList("localhost:3001")), new NetworkConfiguration((short)42), objectContext2);

        minerProcessor.onMinedBlock(blk -> {
            runner1.getNodeProcessor().postMessage(new BlockMessage(blk));
        });

        runner2.getNodeProcessor().onNewBlock(blk -> {
            if (blk.getNumber() > 0)
                semaphore.release();
        });

        minerProcessor.start();
        runner1.start();
        runner2.start();

        semaphore.acquire();

        runner2.stop();
        runner1.stop();
        minerProcessor.stop();

        Block bestBlock = objectContext2.getBlockChain().getBestBlockInformation().getBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertTrue(bestBlock.getNumber() > 0);
    }
}
