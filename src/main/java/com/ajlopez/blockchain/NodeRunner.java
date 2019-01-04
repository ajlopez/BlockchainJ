package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.peers.TcpPeerClient;
import com.ajlopez.blockchain.net.peers.TcpPeerServer;
import com.ajlopez.blockchain.processors.NodeProcessor;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;

import java.io.IOException;
import java.util.List;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class NodeRunner {
    private final boolean miner;
    private final int port;
    private final List<String> peers;

    private final NodeProcessor nodeProcessor;
    private final TcpPeerServer tcpPeerServer;

    public NodeRunner(BlockChain blockChain, boolean miner, int port, List<String> peers, Address coinbase, NetworkConfiguration networkConfiguration) {
        this.miner = miner;
        this.port = port;
        this.peers = peers;

        this.nodeProcessor = new NodeProcessor(networkConfiguration, Peer.createRandomPeer(), blockChain, new TrieStore(new HashMapStore()), coinbase);

        this.tcpPeerServer = port > 0 ? new TcpPeerServer(this.port, this.nodeProcessor) : null;
    }

    public void start() throws IOException {
        System.out.println(String.format("Starting node %s", this.nodeProcessor.getPeer().getId()));

        this.nodeProcessor.startMessagingProcess();

        if (this.port > 0)
            this.tcpPeerServer.start();

        if (this.peers != null && !this.peers.isEmpty())
            for (String peer : this.peers) {
                String[] parts = peer.split(":");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);

                TcpPeerClient client = new TcpPeerClient(host, port, this.nodeProcessor);
                client.connect();
            }

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
