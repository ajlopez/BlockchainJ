package com.ajlopez.blockchain.net.peers;

/**
 * Created by ajlopez on 14/07/2019.
 */
public class Packet {
    private final short protocol;
    private final short network;
    private final byte[] bytes;

    public Packet(short protocol, short network, byte[] bytes) {
        this.protocol = protocol;
        this.network = network;
        this.bytes = bytes;
    }

    public short getProtocol() { return this.protocol; }

    public short getNetwork() { return this.network; }

    public byte[] getBytes() { return this.bytes; }
}
