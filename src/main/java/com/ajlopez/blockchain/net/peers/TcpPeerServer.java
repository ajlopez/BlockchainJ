package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.PeerId;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class TcpPeerServer {
    private final Random random = new Random();
    private final int port;
    private final PeerNode peerNode;

    private boolean started;
    private boolean stopped;

    public TcpPeerServer(int port, PeerNode peerNode) {
        this.port = port;
        this.peerNode = peerNode;
    }

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

                PeerConnection peerConnection = new PeerConnection(peer, clientSocket.getInputStream(), clientSocket.getOutputStream(), this.peerNode);
                peerNode.connectTo(peerConnection);
                peerConnection.start();
            }
        }
        catch (IOException ex) {
            this.stopped = true;
        }
    }
}
