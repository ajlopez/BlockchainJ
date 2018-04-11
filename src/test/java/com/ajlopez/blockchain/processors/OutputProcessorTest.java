package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.StatusMessage;
import com.ajlopez.blockchain.test.simples.SimpleOutputChannel;
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
        Peer peer = new Peer(HashUtilsTest.generateRandomPeerId());
        SimpleOutputChannel channel = new SimpleOutputChannel();

        Message message = new StatusMessage(HashUtilsTest.generateRandomPeerId(), 1, 10);
        Assert.assertFalse(processor.postMessage(peer, message));
        Assert.assertTrue(channel.getMessages().isEmpty());
    }

    @Test
    public void registerPeerAndPostMessage() {
        OutputProcessor processor = new OutputProcessor();
        Peer peer = new Peer(HashUtilsTest.generateRandomPeerId());
        SimpleOutputChannel channel = new SimpleOutputChannel();

        processor.registerPeer(peer, channel);

        Message message = new StatusMessage(HashUtilsTest.generateRandomPeerId(), 1, 10);
        Assert.assertTrue(processor.postMessage(peer, message));
        Assert.assertFalse(channel.getMessages().isEmpty());
    }
}
