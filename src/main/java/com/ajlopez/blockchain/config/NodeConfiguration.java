package com.ajlopez.blockchain.config;

import java.util.List;

/**
 * Created by ajlopez on 01/05/2021.
 */
public class NodeConfiguration {
    private final boolean miner;
    private final int port;
    private final List<String> hosts;

    public NodeConfiguration(boolean miner, int port, List<String> hosts) {
        this.miner = miner;
        this.port = port;
        this.hosts = hosts;
    }

    public boolean isMiner() { return this.miner; }

    public int getPort() { return this.port; }

    public List<String> getHosts() { return this.hosts; }
}
