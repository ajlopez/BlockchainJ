package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.processors.NodeProcessor;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class TcpPeerClient {
    private final Random random = new Random();
    private final NodeProcessor nodeProcesor;
    private final String host;
    private final int port;

    public TcpPeerClient(String host, int port, NodeProcessor nodeProcessor) {
        this.host = host;
        this.port = port;
        this.nodeProcesor = nodeProcessor;
    }

    public void connect() throws IOException {
        Socket socket = new Socket(this.host, this.port);
        byte[] hashBytes = new byte[Hash.BYTES];
        random.nextBytes(hashBytes);
        Peer peer = new Peer(new PeerId(hashBytes));

        PeerConnection peerConnection = new PeerConnection(peer, socket.getInputStream(), socket.getOutputStream(), this.nodeProcesor);
        this.nodeProcesor.connectTo(peer, peerConnection);
        peerConnection.start();
    }
}
