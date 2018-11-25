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
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class NodeRunnerTest {
    @Test
    public void mineBlockUsingOneRunner() throws InterruptedException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        Semaphore semaphore = new Semaphore(0, true);

        blockChain.onBlock(blk -> {
            semaphore.release();
        });

        NodeRunner runner = new NodeRunner(blockChain, true, 0);

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

        NodeRunner runner = new NodeRunner(blockChain, true, 3000);

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
}
