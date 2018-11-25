package com.ajlopez.blockchain.net;

public class Status {
    private PeerId nodeId;
    private long networkNumber;
    private long bestBlockNumber;

    public Status(PeerId nodeId, long networkNumber, long bestBlockNumber) {
        this.nodeId = nodeId;
        this.networkNumber = networkNumber;
        this.bestBlockNumber = bestBlockNumber;
    }

    public PeerId getNodeId() {
        return this.nodeId;
    }

    public long getNetworkNumber() {
        return this.networkNumber;
    }

    public long getBestBlockNumber() {
        return this.bestBlockNumber;
    }
}
