package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.processors.NodeProcessor;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.test.utils.NodesHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
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
        BlockChain blockChain1 = FactoryHelper.createBlockChainWithGenesis();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);
        BlockChain blockChain2 = FactoryHelper.createBlockChainWithGenesis();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);

        Semaphore semaphore = new Semaphore(0, true);

        blockChain2.onBlock(blk -> {
            semaphore.release();
        });

        TcpPeerServer server = new TcpPeerServer((short) 1, 4000, nodeProcessor2);
        server.start();

        TcpPeerClient client = new TcpPeerClient("localhost", 4000, (short)1, nodeProcessor1);
        client.connect();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1, blockChain1.getBestBlock().getHash(), HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000, coinbase);
        Message message = new BlockMessage(block);

        nodeProcessor1.postMessage(FactoryHelper.createRandomPeer(), message);

        NodesHelper.runNodeProcessors(nodeProcessor1, nodeProcessor2);

        semaphore.acquire();

        server.stop();

        Block bestBlock1 = blockChain1.getBestBlock();

        Assert.assertNotNull(bestBlock1);
        Assert.assertEquals(block.getNumber(), bestBlock1.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock1.getHash());

        Block bestBlock2 = blockChain2.getBestBlock();

        Assert.assertNotNull(bestBlock2);
        Assert.assertEquals(block.getNumber(), bestBlock2.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock2.getHash());
    }

    @Test
    public void connectClientServerAndSynchronizeClient() throws IOException, InterruptedException {
        BlockChain blockChain1 = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain1, 10);

        Assert.assertEquals(10, blockChain1.getBestBlockNumber());

        Block block = blockChain1.getBestBlock();

        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);
        BlockChain blockChain2 = FactoryHelper.createBlockChainWithGenesis();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);

        Semaphore semaphore = new Semaphore(0, true);

        blockChain2.onBlock(blk -> {
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

        Block bestBlock1 = blockChain1.getBestBlock();

        Assert.assertNotNull(bestBlock1);
        Assert.assertEquals(block.getNumber(), bestBlock1.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock1.getHash());

        Block bestBlock2 = blockChain2.getBestBlock();

        Assert.assertNotNull(bestBlock2);
        Assert.assertEquals(block.getNumber(), bestBlock2.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock2.getHash());
    }

    @Test
    public void connectClientServerAndSynchronizeServer() throws IOException, InterruptedException {
        BlockChain blockChain1 = FactoryHelper.createBlockChainWithGenesis();

        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);

        BlockChain blockChain2 = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain2, 10);

        Assert.assertEquals(10, blockChain2.getBestBlockNumber());

        Block block = blockChain2.getBestBlock();

        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);

        Semaphore semaphore = new Semaphore(0, true);

        blockChain1.onBlock(blk -> {
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

        Block bestBlock1 = blockChain1.getBestBlock();

        Assert.assertNotNull(bestBlock1);
        Assert.assertEquals(block.getNumber(), bestBlock1.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock1.getHash());

        Block bestBlock2 = blockChain2.getBestBlock();

        Assert.assertNotNull(bestBlock2);
        Assert.assertEquals(block.getNumber(), bestBlock2.getNumber());
        Assert.assertEquals(block.getHash(), bestBlock2.getHash());
    }
}
