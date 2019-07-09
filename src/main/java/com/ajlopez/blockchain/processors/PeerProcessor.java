package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.types.Hash;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 10/02/2018.
 */
public class PeerProcessor {
    private final long networkNumber;
    private final Map<Hash, Long> bestBlocks = new HashMap<>();
    private long bestBlockNumber = BlockChain.NO_BEST_BLOCK_NUMBER;

    public PeerProcessor(long networkNumber) {
        this.networkNumber = networkNumber;
    }

    public long getBestBlockNumber() {
        return this.bestBlockNumber;
    }

    public long getPeerBestBlockNumber(Hash peerId) {
        if (!bestBlocks.containsKey(peerId))
            return BlockChain.NO_BEST_BLOCK_NUMBER;

        return bestBlocks.get(peerId);
    }

    public void registerBestBlockNumber(Hash peerId, long peerNetworkNumber, long bestBlockNumber) {
        if (peerNetworkNumber != this.networkNumber)
            return;

        bestBlocks.put(peerId, bestBlockNumber);

        if (this.bestBlockNumber == BlockChain.NO_BEST_BLOCK_NUMBER || this.bestBlockNumber < bestBlockNumber)
            this.bestBlockNumber = bestBlockNumber;
    }
}
