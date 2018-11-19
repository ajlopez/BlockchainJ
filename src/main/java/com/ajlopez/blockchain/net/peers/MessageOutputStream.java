package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.MessageEncoder;

import java.io.IOException;

/**
 * Created by ajlopez on 18/11/2018.
 */
public class MessageOutputStream {
    private PacketOutputStream packetOutputStream;

    public MessageOutputStream(PacketOutputStream packetOutputStream) {
        this.packetOutputStream = packetOutputStream;
    }

    public void writeMessage(Message message) {
        byte[] bytes = MessageEncoder.encode(message);

        this.packetOutputStream.writePacket(bytes);
    }

    public void close() throws IOException {
        this.packetOutputStream.close();
    }
}
