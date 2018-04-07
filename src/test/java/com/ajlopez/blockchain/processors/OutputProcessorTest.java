package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.StatusMessage;
import com.ajlopez.blockchain.test.simples.SimplePeer;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 06/04/2018.
 */
public class OutputProcessorTest {
    @Test
    public void registerPeerAndPostMessage() {
        OutputProcessor processor = new OutputProcessor();
        SimplePeer peer = new SimplePeer(HashUtilsTest.generateRandomPeerId());

        processor.registerPeer(peer, peer);

        Message message = new StatusMessage(HashUtilsTest.generateRandomPeerId(), 1, 10);
        processor.postMessage(peer, message);

        Assert.assertFalse(peer.getMessages().isEmpty());
    }
}
