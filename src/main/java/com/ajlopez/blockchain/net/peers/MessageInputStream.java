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
        byte[] bytes = this.packetInputStream.readPacket();

        return MessageEncoder.decode(bytes);
    }
}
