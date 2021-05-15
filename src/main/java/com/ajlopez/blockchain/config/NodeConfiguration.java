package com.ajlopez.blockchain.config;

import java.util.List;

/**
 * Created by ajlopez on 01/05/2021.
 */
public class NodeConfiguration {
    private final int port;
    private final List<String> hosts;

    public NodeConfiguration(int port, List<String> hosts) {
        this.port = port;
        this.hosts = hosts;
    }

    public boolean isTcpServer() { return this.port > 0; }

    public int getPort() { return this.port; }

    public List<String> getHosts() { return this.hosts; }
}
