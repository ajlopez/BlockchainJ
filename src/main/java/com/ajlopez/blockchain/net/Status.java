package com.ajlopez.blockchain.net;

public class Status {
    private PeerId peerId;
    private long networkNumber;
    private long bestBlockNumber;

    public Status(PeerId peerId, long networkNumber, long bestBlockNumber) {
        this.peerId = peerId;
        this.networkNumber = networkNumber;
        this.bestBlockNumber = bestBlockNumber;
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
}
