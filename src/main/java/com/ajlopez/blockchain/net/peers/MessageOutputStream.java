package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.MessageEncoder;

import java.io.IOException;

/**
 * Created by ajlopez on 18/11/2018.
 */
public class MessageOutputStream {
    private final short network;
    private final PacketOutputStream packetOutputStream;

    public MessageOutputStream(short network, PacketOutputStream packetOutputStream) {
        this.network = network;
        this.packetOutputStream = packetOutputStream;
    }

    public boolean writeMessage(Peer sender, Message message) {
        byte[] bytes = MessageEncoder.encode(message);

        // TODO sign packet using sender keys

        return this.packetOutputStream.writePacket(new Packet(Protocols.BLOCKCHAIN, network, bytes));
    }

    public void close() throws IOException {
        this.packetOutputStream.close();
    }
}
