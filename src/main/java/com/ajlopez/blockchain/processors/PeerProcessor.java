package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.types.Hash;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 10/02/2018.
 */
public class PeerProcessor {
    public static final long NO_BEST_BLOCK_NUMBER = -1;

    private Map<Hash, Long> bestBlocks = new HashMap<>();
    private long bestBlockNumber = NO_BEST_BLOCK_NUMBER;

    public long getBestBlockNumber() {
        return this.bestBlockNumber;
    }

    public long getPeerBestBlockNumber(Hash peerId) {
        if (!bestBlocks.containsKey(peerId))
            return NO_BEST_BLOCK_NUMBER;

        return bestBlocks.get(peerId);
    }

    public void registerBestBlockNumber(Hash peerId, long bestBlockNumber) {
        bestBlocks.put(peerId, bestBlockNumber);

        if (this.bestBlockNumber == NO_BEST_BLOCK_NUMBER || this.bestBlockNumber < bestBlockNumber)
            this.bestBlockNumber = bestBlockNumber;
    }
}
