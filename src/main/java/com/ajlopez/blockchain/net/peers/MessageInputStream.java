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
        while (true) {
            Packet packet = this.packetInputStream.readPacket();

            if (packet.getNetwork() == this.network)
                return MessageEncoder.decode(packet.getBytes());
        }
    }
}
