package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 10/02/2018.
 */
public class PeerProcessorTest {
    @Test
    public void noBestBlockNumberInNewPeer() {
        PeerProcessor processor = new PeerProcessor(1);
        PeerId peerId = FactoryHelper.createRandomPeerId();

        Assert.assertNull(processor.getStatus(peerId));
    }

    @Test
    public void registerPeerBestBlockNumber() {
        PeerProcessor processor = new PeerProcessor(1);
        long bestBlockNumber = 100;
        PeerId peerId = FactoryHelper.createRandomPeerId();
        Status status = new Status(peerId, 1, bestBlockNumber, FactoryHelper.createRandomBlockHash(), Difficulty.ONE);

        processor.registerStatus(status);

        Status result = processor.getStatus(peerId);

        Assert.assertNotNull(result);
        Assert.assertSame(status, result);
    }

    @Test
    public void registerPeerBestBlockNumberFromAnotherNetworkNumber() {
        PeerProcessor processor = new PeerProcessor(1);
        PeerId peerId = FactoryHelper.createRandomPeerId();
        long bestBlockNumber = 100;

        Status status = new Status(peerId, 2, bestBlockNumber, FactoryHelper.createRandomBlockHash(), Difficulty.ONE);

        processor.registerStatus(status);

        Assert.assertNull(processor.getStatus(peerId));
    }
}
