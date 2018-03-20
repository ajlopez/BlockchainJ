package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.core.types.Hash;
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
}
