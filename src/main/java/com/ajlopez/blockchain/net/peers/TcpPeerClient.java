package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.messages.StatusMessage;
import com.ajlopez.blockchain.processors.NodeProcessor;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class TcpPeerClient {
    private final short network;
    private final NodeProcessor peerNode;
    private final String host;
    private final int port;

    public TcpPeerClient(String host, int port, short network, NodeProcessor peerNode) {
        this.host = host;
        this.port = port;
        this.network = network;
        this.peerNode = peerNode;
    }

    public PeerNode connect() throws IOException {
        Socket socket = new Socket(this.host, this.port);
        Peer peer = Peer.createRandomPeer();

        PeerConnection peerConnection = new PeerConnection(this.network, peer, socket.getInputStream(), socket.getOutputStream(), this.peerNode);

        if (this.peerNode != null) {
            peerConnection.postMessage(this.peerNode.getPeer(), new StatusMessage(this.peerNode.getStatus()));
            this.peerNode.connectTo(peerConnection);
        }

        peerConnection.start();

        return peerConnection;
    }
}
