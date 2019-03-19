package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.core.types.BlockHash;

public class Status {
    private PeerId peerId;
    private long networkNumber;
    private long bestBlockNumber;
    private BlockHash bestBlockHash;

    public Status(PeerId peerId, long networkNumber, long bestBlockNumber, BlockHash bestBlockHash) {
        this.peerId = peerId;
        this.networkNumber = networkNumber;
        this.bestBlockNumber = bestBlockNumber;
        this.bestBlockHash = bestBlockHash;
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
}
