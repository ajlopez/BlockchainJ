package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 04/02/2018.
 */
public class PeerTest {
    @Test
    public void createWithPeerId() {
        PeerId peerId = HashUtilsTest.generateRandomPeerId();

        Peer peer = new Peer(peerId);

        Assert.assertEquals(peerId, peer.getId());
    }

    @Test
    public void peerEquals() {
        PeerId peerId = HashUtilsTest.generateRandomPeerId();

        Peer peer1 = new Peer(peerId);
        Peer peer2 = new Peer(peerId);
        Peer peer3 = new Peer(HashUtilsTest.generateRandomPeerId());

        Assert.assertEquals(peer1, peer1);
        Assert.assertEquals(peer1, peer2);
        Assert.assertEquals(peer2, peer1);
        Assert.assertNotEquals(peer1, peer3);
        Assert.assertNotEquals(peer3, peer2);
        Assert.assertNotEquals(peer1, null);
        Assert.assertNotEquals(peer1, "foo");
        Assert.assertNotEquals(peer1, peerId);

        Assert.assertEquals(peer1.hashCode(), peer2.hashCode());
        Assert.assertNotEquals(peer1.hashCode(), peer3.hashCode());
    }
}
