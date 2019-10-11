package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.messages.StatusMessage;
import com.ajlopez.blockchain.processors.NodeProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class TcpPeerServer {
    private final short network;
    private final int port;
    private final NodeProcessor peerNode;

    private boolean started;
    private boolean stopped;

    public TcpPeerServer(short network, int port, NodeProcessor peerNode) {
        this.network = network;
        this.port = port;
        this.peerNode = peerNode;
    }

    public short getNetwork() { return this.network; }

    public synchronized void start() {
        if (started)
            return;

        new Thread(this::process).start();

        this.started = true;
    }

    public void stop() {
        this.stopped = true;
    }

    private void process() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);

            while (!this.stopped) {
                Socket clientSocket = serverSocket.accept();
                Peer peer = Peer.createRandomPeer();

                PeerConnection peerConnection = new PeerConnection(this.network, peer, clientSocket.getInputStream(), clientSocket.getOutputStream(), this.peerNode);
                peerConnection.postMessage(this.peerNode.getPeer(), new StatusMessage(this.peerNode.getStatus()));
                this.peerNode.connectTo(peerConnection);
                peerConnection.start();
            }
        }
        catch (IOException ex) {
            this.stopped = true;
        }
    }
}
