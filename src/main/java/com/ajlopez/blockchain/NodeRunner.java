package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.peers.TcpPeerClient;
import com.ajlopez.blockchain.net.peers.TcpPeerServer;
import com.ajlopez.blockchain.processors.NodeProcessor;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class NodeRunner {
    private boolean miner;
    private int port;

    private NodeProcessor nodeProcessor;
    private TcpPeerServer tcpPeerServer;

    public NodeRunner(BlockChain blockChain, boolean miner, int port) {
        this.miner = miner;
        this.port = port;

        this.nodeProcessor = new NodeProcessor(Peer.createRandomPeer(), blockChain);

        if (this.port > 0)
            this.tcpPeerServer = new TcpPeerServer(this.port, this.nodeProcessor);
    }

    public void start() {
        System.out.println(String.format("Starting node %s", this.nodeProcessor.getPeer().getId()));

        this.nodeProcessor.startMessagingProcess();

        if (this.port > 0)
            this.tcpPeerServer.start();

        if (this.miner)
            this.nodeProcessor.startMiningProcess();
    }

    public void stop() {
        if (this.miner)
            this.nodeProcessor.stopMiningProcess();

        if (this.port > 0)
            this.tcpPeerServer.stop();

        this.nodeProcessor.stopMessagingProcess();

        System.out.println(String.format("Stopping node %s", this.nodeProcessor.getPeer().getId()));
    }
}
