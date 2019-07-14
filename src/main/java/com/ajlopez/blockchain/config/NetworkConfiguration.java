package com.ajlopez.blockchain.config;

/**
 * Created by ajlopez on 30/12/2018.
 */
public class NetworkConfiguration {
    private final short networkNumber;

    public NetworkConfiguration(short networkNumber) {
        this.networkNumber = networkNumber;
    }

    public short getNetworkNumber() {
        return this.networkNumber;
    }
}
