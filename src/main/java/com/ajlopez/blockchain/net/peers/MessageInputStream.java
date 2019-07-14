package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.MessageEncoder;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class MessageInputStream {
    private PacketInputStream packetInputStream;

    public MessageInputStream(PacketInputStream packetInputStream) {
        this.packetInputStream = packetInputStream;
    }

    public Message readMessage() {
        // TODO process network, protocol
        byte[] bytes = this.packetInputStream.readPacket().getBytes();

        return MessageEncoder.decode(bytes);
    }
}
