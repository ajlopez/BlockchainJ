package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.MessageEncoder;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class MessageInputStream {
    private final short network;
    private final PacketInputStream packetInputStream;

    public MessageInputStream(short network, PacketInputStream packetInputStream) {
        this.network = network;
        this.packetInputStream = packetInputStream;
    }

    public Message readMessage() {
        // TODO process protocol
        Packet packet = this.packetInputStream.readPacket();

        if (packet == null)
            return null;

        if (packet.getNetwork() != this.network)
            this.packetInputStream.close();

        return MessageEncoder.decode(packet.getBytes());
    }
}
