package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.StatusMessage;
import com.ajlopez.blockchain.test.simples.SimpleOutputChannel;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * Created by ajlopez on 06/04/2018.
 */
public class SendProcessorTest {
    @Test
    public void connectAndDisconnectPeer() {
        SendProcessor processor = new SendProcessor();
        Peer peer = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();

        processor.connectToPeer(peer, channel);

        Assert.assertTrue(processor.isConnected(peer));

        processor.disconnectFromPeer(peer);

        Assert.assertFalse(processor.isConnected(peer));
    }

    @Test
    public void postMessageToNotConnectedPeer() {
        SendProcessor processor = new SendProcessor();
        Peer peer = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();

        Message message = new StatusMessage(new Status(HashUtilsTest.generateRandomPeerId(), 1, 10));
        Assert.assertFalse(processor.postMessage(peer, message));
        Assert.assertTrue(channel.getMessages().isEmpty());
    }

    @Test
    public void postMessageWhenNoPeerIsConnected() {
        SendProcessor processor = new SendProcessor();

        SimpleOutputChannel channel = new SimpleOutputChannel();

        Message message = new StatusMessage(new Status(HashUtilsTest.generateRandomPeerId(), 1, 10));
        Assert.assertEquals(0, processor.postMessage(message));
    }

    @Test
    public void connectToPeerAndPostMessage() {
        SendProcessor processor = new SendProcessor();
        Peer peer = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();

        processor.connectToPeer(peer, channel);

        Message message = new StatusMessage(new Status(HashUtilsTest.generateRandomPeerId(), 1, 10));
        Assert.assertTrue(processor.postMessage(peer, message));
        Assert.assertFalse(channel.getMessages().isEmpty());
    }

    @Test
    public void connectToPeersAndPostMessage() {
        SendProcessor processor = new SendProcessor();
        Peer peer1 = FactoryHelper.createPeer();
        SimpleOutputChannel channel1 = new SimpleOutputChannel();
        Peer peer2 = FactoryHelper.createPeer();
        SimpleOutputChannel channel2 = new SimpleOutputChannel();

        processor.connectToPeer(peer1, channel1);
        processor.connectToPeer(peer2, channel2);

        Message message = new StatusMessage(new Status(HashUtilsTest.generateRandomPeerId(), 1, 10));

        Assert.assertEquals(2, processor.postMessage(message));

        Assert.assertFalse(channel1.getMessages().isEmpty());
        Assert.assertFalse(channel2.getMessages().isEmpty());
    }

    @Test
    public void connectToPeersAndPostMessageSkippingOne() {
        SendProcessor processor = new SendProcessor();
        Peer peer1 = FactoryHelper.createPeer();
        SimpleOutputChannel channel1 = new SimpleOutputChannel();
        Peer peer2 = FactoryHelper.createPeer();
        SimpleOutputChannel channel2 = new SimpleOutputChannel();

        processor.connectToPeer(peer1, channel1);
        processor.connectToPeer(peer2, channel2);

        Message message = new StatusMessage(new Status(HashUtilsTest.generateRandomPeerId(), 1, 10));

        Assert.assertEquals(1, processor.postMessage(message, Collections.singletonList(peer2.getId())));

        Assert.assertFalse(channel1.getMessages().isEmpty());
        Assert.assertTrue(channel2.getMessages().isEmpty());
    }

    @Test
    public void connectToPeerAndPostMessageToAnotherPeer() {
        SendProcessor processor = new SendProcessor();
        Peer peer = FactoryHelper.createPeer();
        Peer peer2 = FactoryHelper.createPeer();
        SimpleOutputChannel channel = new SimpleOutputChannel();

        processor.connectToPeer(peer, channel);

        Message message = new StatusMessage(new Status(HashUtilsTest.generateRandomPeerId(), 1, 10));
        Assert.assertFalse(processor.postMessage(peer2, message));
        Assert.assertTrue(channel.getMessages().isEmpty());
    }
}
