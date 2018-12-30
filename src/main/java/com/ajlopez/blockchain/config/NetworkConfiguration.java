package com.ajlopez.blockchain.config;

/**
 * Created by ajlopez on 30/12/2018.
 */
public class NetworkConfiguration {
    private final int networkNumber;

    public NetworkConfiguration(int networkNumber) {
        this.networkNumber = networkNumber;
    }

    public int getNetworkNumber() {
        return this.networkNumber;
    }
}
