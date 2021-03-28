package com.ajlopez.blockchain;

import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.peers.TcpPeerClient;
import com.ajlopez.blockchain.net.peers.TcpPeerServer;
import com.ajlopez.blockchain.processors.NodeProcessor;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.store.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class NodeRunner {
    private final boolean miner;
    private final int port;
    private final short network;
    private final List<String> peers;

    private final NodeProcessor nodeProcessor;
    private final TcpPeerServer tcpPeerServer;

    public NodeRunner(boolean miner, int port, List<String> peers, Address coinbase, NetworkConfiguration networkConfiguration, KeyValueStores keyValueStores, TransactionPool transactionPool) {
        this.miner = miner;
        this.port = port;
        this.peers = peers;
        this.network = networkConfiguration.getNetworkNumber();

        this.nodeProcessor = new NodeProcessor(networkConfiguration, Peer.createRandomPeer(), keyValueStores, coinbase, transactionPool);
        this.tcpPeerServer = port > 0 ? new TcpPeerServer(networkConfiguration.getNetworkNumber() ,this.port, this.nodeProcessor) : null;
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

                TcpPeerClient client = new TcpPeerClient(host, port, this.network, this.nodeProcessor);
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

    public void onNewBlock(Consumer<Block> consumer) {
        this.nodeProcessor.onNewBlock(consumer);
    }

    public void onNewBestBlock(Consumer<Block> consumer) {
        this.nodeProcessor.onNewBestBlock(consumer);
    }
}
