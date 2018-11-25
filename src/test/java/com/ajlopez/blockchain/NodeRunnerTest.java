package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.peers.PeerNode;
import com.ajlopez.blockchain.net.peers.TcpPeerClient;
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
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        Semaphore semaphore = new Semaphore(0, true);

        blockChain.onBlock(blk -> {
            semaphore.release();
        });

        NodeRunner runner = new NodeRunner(blockChain, true, 0, Collections.emptyList());

        runner.start();

        semaphore.acquire();

        runner.stop();

        Block bestBlock = blockChain.getBestBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertTrue(bestBlock.getNumber() > 0);
    }

    @Test
    public void processBlockInServerRunner() throws InterruptedException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        Semaphore semaphore = new Semaphore(0, true);

        blockChain.onBlock(blk -> {
            semaphore.release();
        });

        NodeRunner runner = new NodeRunner(blockChain, true, 3000, Collections.emptyList());

        runner.start();

        Block block = new Block(1, blockChain.getBestBlock().getHash());
        Message message = new BlockMessage(block);

        TcpPeerClient tcpPeerClient = new TcpPeerClient("127.0.0.1", 3000, null);

        PeerNode peerNode = tcpPeerClient.connect();

        peerNode.postMessage(peerNode.getPeer(), message);

        semaphore.acquire();

        runner.stop();

        Block bestBlock = blockChain.getBestBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertEquals(1, bestBlock.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock.getHash());
    }

    @Test
    public void connectTwoNodeRunners() throws InterruptedException, IOException {
        BlockChain blockChain1 = FactoryHelper.createBlockChainWithGenesis();
        BlockChain blockChain2 = FactoryHelper.createBlockChainWithGenesis();

        Semaphore semaphore = new Semaphore(0, true);

        blockChain2.onBlock(blk -> {
            semaphore.release();
        });

        NodeRunner runner1 = new NodeRunner(blockChain1, true, 3001, null);
        NodeRunner runner2 = new NodeRunner(blockChain2, false, 0, Collections.singletonList("localhost:3001"));

        runner1.start();
        runner2.start();

        semaphore.acquire();

        runner2.stop();
        runner1.stop();

        Block bestBlock = blockChain2.getBestBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertTrue(bestBlock.getNumber() > 0);
    }
}
