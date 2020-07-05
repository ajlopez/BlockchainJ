package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;

public class Status {
    private final PeerId peerId;
    private final long networkNumber;
    private final long bestBlockNumber;
    private final BlockHash bestBlockHash;
    private final Difficulty bestTotalDifficulty;

    public Status(PeerId peerId, long networkNumber, long bestBlockNumber, BlockHash bestBlockHash, Difficulty bestTotalDifficulty) {
        this.peerId = peerId;
        this.networkNumber = networkNumber;
        this.bestBlockNumber = bestBlockNumber;
        this.bestBlockHash = bestBlockHash;
        this.bestTotalDifficulty = bestTotalDifficulty;
    }

    public PeerId getPeerId() {
        return this.peerId;
    }

    public long getNetworkNumber() {
        return this.networkNumber;
    }

    public long getBestBlockNumber() {
        return this.bestBlockNumber;
    }

    public BlockHash getBestBlockHash() { return this.bestBlockHash; }

    public Difficulty getBestTotalDifficulty() { return this.bestTotalDifficulty; }
}
