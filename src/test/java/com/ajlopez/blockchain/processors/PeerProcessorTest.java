package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 10/02/2018.
 */
public class PeerProcessorTest {
    @Test
    public void noBestBlockNumber() {
        PeerProcessor processor = new PeerProcessor();

        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getBestBlockNumber());
    }

    @Test
    public void noBestBlockNumberInNewPeer() {
        PeerProcessor processor = new PeerProcessor();
        Hash peerId = HashUtilsTest.generateRandomHash();

        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getPeerBestBlockNumber(peerId));
    }

    @Test
    public void registerPeerBestBlockNumber() {
        PeerProcessor processor = new PeerProcessor();
        Hash peerId = HashUtilsTest.generateRandomHash();
        long bestBlockNumber = 100;

        processor.registerBestBlockNumber(peerId, bestBlockNumber);

        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getPeerBestBlockNumber(HashUtilsTest.generateRandomHash()));
        Assert.assertEquals(bestBlockNumber, processor.getPeerBestBlockNumber(peerId));
        Assert.assertEquals(bestBlockNumber, processor.getBestBlockNumber());
    }

    @Test
    public void registerTwoPeersBestBlockNumber() {
        PeerProcessor processor = new PeerProcessor();
        Hash peerId1 = HashUtilsTest.generateRandomHash();
        long bestBlockNumber1 = 100;
        Hash peerId2 = HashUtilsTest.generateRandomHash();
        long bestBlockNumber2 = 50;

        processor.registerBestBlockNumber(peerId1, bestBlockNumber1);
        processor.registerBestBlockNumber(peerId2, bestBlockNumber2);

        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, processor.getPeerBestBlockNumber(HashUtilsTest.generateRandomHash()));
        Assert.assertEquals(bestBlockNumber1, processor.getPeerBestBlockNumber(peerId1));
        Assert.assertEquals(bestBlockNumber2, processor.getPeerBestBlockNumber(peerId2));
        Assert.assertEquals(bestBlockNumber1, processor.getBestBlockNumber());
    }
}
