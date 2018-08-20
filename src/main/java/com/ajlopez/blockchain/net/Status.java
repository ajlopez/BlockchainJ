package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.core.types.Hash;

public class Status {
    private Hash nodeId;
    private long networkNumber;
    private long bestBlockNumber;

    public Status(Hash nodeId, long networkNumber, long bestBlockNumber) {
        this.nodeId = nodeId;
        this.networkNumber = networkNumber;
        this.bestBlockNumber = bestBlockNumber;
    }

    public Hash getNodeId() {
        return this.nodeId;
    }

    public long getNetworkNumber() {
        return this.networkNumber;
    }

    public long getBestBlockNumber() {
        return this.bestBlockNumber;
    }
}
