package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.ObjectContext;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.config.NodeConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.peers.TcpPeerClient;
import com.ajlopez.blockchain.net.peers.TcpPeerServer;
import com.ajlopez.blockchain.processors.NodeProcessor;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class NodeRunner {
    private final NodeConfiguration nodeConfiguration;

    private final short network;

    private final NodeProcessor nodeProcessor;
    private final TcpPeerServer tcpPeerServer;

    public NodeRunner(NodeConfiguration nodeConfiguration, NetworkConfiguration networkConfiguration, ObjectContext objectContext) {
        this.nodeConfiguration = nodeConfiguration;
        this.network = networkConfiguration.getNetworkNumber();

        this.nodeProcessor = new NodeProcessor(networkConfiguration, Peer.createRandomPeer(), objectContext);
        this.tcpPeerServer = this.nodeConfiguration.getPort() > 0 ? new TcpPeerServer(networkConfiguration.getNetworkNumber() ,this.nodeConfiguration.getPort(), this.nodeProcessor) : null;
    }

    public void start() throws IOException {
        System.out.println(String.format("Starting node %s", this.nodeProcessor.getPeer().getId()));

        this.nodeProcessor.startMessagingProcess();

        if (this.nodeConfiguration.getPort() > 0)
            this.tcpPeerServer.start();

        if (this.nodeConfiguration.getHosts() != null && !this.nodeConfiguration.getHosts().isEmpty())
            for (String peer : this.nodeConfiguration.getHosts()) {
                String[] parts = peer.split(":");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);

                TcpPeerClient client = new TcpPeerClient(host, port, this.network, this.nodeProcessor);
                client.connect();
            }
    }

    public void stop() {
        if (this.nodeConfiguration.getPort() > 0)
            this.tcpPeerServer.stop();

        this.nodeProcessor.stopMessagingProcess();

        System.out.println(String.format("Stopping node %s", this.nodeProcessor.getPeer().getId()));
    }

    public void onNewBlock(Consumer<Block> consumer) {
        this.nodeProcessor.onNewBlock(consumer);
    }

    public NodeProcessor getNodeProcessor() {
        return this.nodeProcessor;
    }
}
