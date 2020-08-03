package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.Status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 10/02/2018.
 */
public class PeerProcessor {
    private final long networkNumber;
    private final Map<PeerId, Status> statuses = new HashMap<>();

    public PeerProcessor(long networkNumber) {
        this.networkNumber = networkNumber;
    }

    public long getNetworkNumber() { return this.networkNumber; }

    public Status getStatus(PeerId peerId) {
        return statuses.get(peerId);
    }

    public void registerStatus(Status status) {
        if (status.getNetworkNumber() != this.networkNumber)
            return;

        statuses.put(status.getPeerId(), status);
    }
}
