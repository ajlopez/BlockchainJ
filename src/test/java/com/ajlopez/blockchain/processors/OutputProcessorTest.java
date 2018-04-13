package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.StatusMessage;
import com.ajlopez.blockchain.test.simples.SimpleOutputChannel;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 06/04/2018.
 */
public class OutputProcessorTest {
    @Test
    public void postMessageToUnregisteredPeer() {
        OutputProcessor processor = new OutputProcessor();
        Peer peer = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();

        Message message = new StatusMessage(HashUtilsTest.generateRandomPeerId(), 1, 10);
        Assert.assertFalse(processor.postMessage(peer, message));
        Assert.assertTrue(channel.getMessages().isEmpty());
    }

    @Test
    public void postMessageToNoPeer() {
        OutputProcessor processor = new OutputProcessor();

        SimpleOutputChannel channel = new SimpleOutputChannel();

        Message message = new StatusMessage(HashUtilsTest.generateRandomPeerId(), 1, 10);
        Assert.assertFalse(processor.postMessage(message));
    }

    @Test
    public void registerPeerAndPostMessage() {
        OutputProcessor processor = new OutputProcessor();
        Peer peer = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();

        processor.registerPeer(peer, channel);

        Message message = new StatusMessage(HashUtilsTest.generateRandomPeerId(), 1, 10);
        Assert.assertTrue(processor.postMessage(peer, message));
        Assert.assertFalse(channel.getMessages().isEmpty());
    }

    @Test
    public void registerPeersAndPostMessage() {
        OutputProcessor processor = new OutputProcessor();
        Peer peer1 = new Peer(HashUtilsTest.generateRandomPeerId());
        SimpleOutputChannel channel1 = new SimpleOutputChannel();
        Peer peer2 = new Peer(HashUtilsTest.generateRandomPeerId());
        SimpleOutputChannel channel2 = new SimpleOutputChannel();

        processor.registerPeer(peer1, channel1);
        processor.registerPeer(peer2, channel2);

        Message message = new StatusMessage(HashUtilsTest.generateRandomPeerId(), 1, 10);

        Assert.assertTrue(processor.postMessage(message));

        Assert.assertFalse(channel1.getMessages().isEmpty());
        Assert.assertTrue(processor.postMessage(peer2, message));
        Assert.assertFalse(channel2.getMessages().isEmpty());
    }

    @Test
    public void registerPeerAndPostMessageToAnotherPeer() {
        OutputProcessor processor = new OutputProcessor();
        Peer peer = new Peer(HashUtilsTest.generateRandomPeerId());
        Peer peer2 = new Peer(HashUtilsTest.generateRandomPeerId());
        SimpleOutputChannel channel = new SimpleOutputChannel();

        processor.registerPeer(peer, channel);

        Message message = new StatusMessage(HashUtilsTest.generateRandomPeerId(), 1, 10);
        Assert.assertFalse(processor.postMessage(peer2, message));
        Assert.assertTrue(channel.getMessages().isEmpty());
    }
}
