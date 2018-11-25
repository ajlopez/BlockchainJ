package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.processors.NodeProcessor;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class NodeRunner {
    private boolean miner;
    private NodeProcessor nodeProcessor;

    public NodeRunner(BlockChain blockChain, boolean miner) {
        this.miner = miner;
        this.nodeProcessor = new NodeProcessor(Peer.createRandomPeer(), blockChain);
    }

    public void start() {
        this.nodeProcessor.startMessagingProcess();

        if (this.miner)
            this.nodeProcessor.startMiningProcess();
    }

    public void stop() {
        if (this.miner)
            this.nodeProcessor.stopMiningProcess();

        this.nodeProcessor.stopMessagingProcess();
    }
}
