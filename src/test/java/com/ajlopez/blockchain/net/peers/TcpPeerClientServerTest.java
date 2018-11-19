package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.processors.NodeProcessor;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class TcpPeerClientServerTest {
    @Test
    public void connectClientServer() throws IOException, InterruptedException {
        BlockChain blockChain1 = new BlockChain();
        NodeProcessor nodeProcessor1 = FactoryHelper.createNodeProcessor(blockChain1);
        BlockChain blockChain2 = new BlockChain();
        NodeProcessor nodeProcessor2 = FactoryHelper.createNodeProcessor(blockChain2);

        TcpPeerServer server = new TcpPeerServer(3000, nodeProcessor2);
        server.start();

        TcpPeerClient client = new TcpPeerClient("localhost", 3000, nodeProcessor1);
        client.connect();

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        nodeProcessor1.postMessage(FactoryHelper.createPeer(), message);

        runNodeProcessors(nodeProcessor1, nodeProcessor2);

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

    private static void runNodeProcessors(NodeProcessor ...nodeProcessors) throws InterruptedException {
        List<Semaphore> semaphores = new ArrayList<>();

        for (NodeProcessor nodeProcessor : nodeProcessors) {
            Semaphore semaphore = new Semaphore(0, true);

            nodeProcessor.onEmpty(() -> {
                semaphore.release();
            });

            semaphores.add(semaphore);
        }

        for (NodeProcessor nodeProcessor : nodeProcessors)
            nodeProcessor.start();

        for (Semaphore semaphore : semaphores)
            semaphore.acquire();

        for (NodeProcessor nodeProcessor : nodeProcessors)
            nodeProcessor.stop();
    }
}
