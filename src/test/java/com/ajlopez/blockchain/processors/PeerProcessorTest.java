package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 10/02/2018.
 */
public class PeerProcessorTest {
    @Test
    public void noBestBlockNumber() {
        PeerProcessor processor = new PeerProcessor(1);

        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getBestBlockNumber());
    }

    @Test
    public void noBestBlockNumberInNewPeer() {
        PeerProcessor processor = new PeerProcessor(1);
        Hash peerId = FactoryHelper.createRandomHash();

        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getPeerBestBlockNumber(peerId));
    }

    @Test
    public void registerPeerBestBlockNumber() {
        PeerProcessor processor = new PeerProcessor(1);
        Hash peerId = FactoryHelper.createRandomHash();
        long bestBlockNumber = 100;

        processor.registerBestBlockNumber(peerId, 1, bestBlockNumber);

        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getPeerBestBlockNumber(FactoryHelper.createRandomHash()));
        Assert.assertEquals(bestBlockNumber, processor.getPeerBestBlockNumber(peerId));
        Assert.assertEquals(bestBlockNumber, processor.getBestBlockNumber());
    }

    @Test
    public void registerPeerBestBlockNumberFromAnotherNetworkNumber() {
        PeerProcessor processor = new PeerProcessor(1);
        Hash peerId = FactoryHelper.createRandomHash();
        long bestBlockNumber = 100;

        processor.registerBestBlockNumber(peerId, 2, bestBlockNumber);

        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getPeerBestBlockNumber(FactoryHelper.createRandomHash()));
        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getPeerBestBlockNumber(peerId));
        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getBestBlockNumber());
    }

    @Test
    public void registerTwoPeersBestBlockNumber() {
        PeerProcessor processor = new PeerProcessor(1);
        Hash peerId1 = FactoryHelper.createRandomHash();
        long bestBlockNumber1 = 100;
        Hash peerId2 = FactoryHelper.createRandomHash();
        long bestBlockNumber2 = 50;

        processor.registerBestBlockNumber(peerId1, 1, bestBlockNumber1);
        processor.registerBestBlockNumber(peerId2, 1, bestBlockNumber2);

        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getPeerBestBlockNumber(FactoryHelper.createRandomHash()));
        Assert.assertEquals(bestBlockNumber1, processor.getPeerBestBlockNumber(peerId1));
        Assert.assertEquals(bestBlockNumber2, processor.getPeerBestBlockNumber(peerId2));
        Assert.assertEquals(bestBlockNumber1, processor.getBestBlockNumber());
    }
}
