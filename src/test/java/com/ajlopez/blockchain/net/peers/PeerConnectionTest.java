package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.messages.GetBlockByHashMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.MessageEncoder;
import com.ajlopez.blockchain.test.simples.SimpleMessageChannel;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class PeerConnectionTest {
    @Test
    public void writeAndReadMessage() throws IOException, InterruptedException {
        PipedOutputStream outputStream1 = new PipedOutputStream();
        PipedInputStream inputStream1 = new PipedInputStream();
        inputStream1.connect(outputStream1);

        PipedOutputStream outputStream2 = new PipedOutputStream();
        PipedInputStream inputStream2 = new PipedInputStream();
        inputStream2.connect(outputStream2);

        Peer peer1 = FactoryHelper.createPeer();
        Peer peer2 = FactoryHelper.createPeer();

        SimpleMessageChannel inputChannel = new SimpleMessageChannel();

        PeerConnection peerConnection1 = new PeerConnection(peer2, inputStream1, outputStream2, null);
        PeerConnection peerConnection2 = new PeerConnection(peer1, inputStream2, outputStream1, inputChannel);

        Message message = new GetBlockByHashMessage(HashUtilsTest.generateRandomBlockHash());

        Semaphore semaphore = new Semaphore(0, true);

        inputChannel.onMessage((pair) -> {
            semaphore.release();
        });

        peerConnection1.start();
        peerConnection2.start();

        peerConnection1.postMessage(FactoryHelper.createPeer(), message);

        semaphore.acquire();

        peerConnection1.stop();
        peerConnection2.stop();

        Assert.assertEquals(1, inputChannel.getPeerMessages().size());
        Assert.assertEquals(peer1.getId(), inputChannel.getPeerMessages().get(0).getKey().getId());
        Assert.assertArrayEquals(MessageEncoder.encode(message), MessageEncoder.encode(inputChannel.getPeerMessages().get(0).getValue()));
    }
}
