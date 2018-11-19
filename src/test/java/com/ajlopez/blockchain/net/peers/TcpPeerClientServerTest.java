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

        nodeProcessor1.start();
        nodeProcessor2.start();

        Thread.sleep(2000);

        nodeProcessor2.stop();
        nodeProcessor2.stop();

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
