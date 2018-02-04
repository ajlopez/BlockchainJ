package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;

/**
 * Created by ajlopez on 04/02/2018.
 */
public class StatusMessage extends Message {
    private Hash nodeId;
    private long networkNumber;
    private long bestBlockNumber;

    public StatusMessage(Hash nodeId, long networkNumber, long bestBlockNumber) {
        super(MessageType.STATUS);
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

    @Override
    public byte[] getPayload() {
        return null;
    }
}
