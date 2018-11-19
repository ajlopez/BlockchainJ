package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.processors.NodeProcessor;

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
    private final NodeProcessor nodeProcessor;

    private boolean started;
    private boolean stopped;

    public TcpPeerServer(int port, NodeProcessor nodeProcessor) {
        this.port = port;
        this.nodeProcessor = nodeProcessor;
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
                byte[] hashBytes = new byte[Hash.BYTES];
                random.nextBytes(hashBytes);
                Peer peer = new Peer(new PeerId(hashBytes));

                PeerConnection peerConnection = new PeerConnection(peer, clientSocket.getInputStream(), clientSocket.getOutputStream(), this.nodeProcessor);
                nodeProcessor.connectTo(peer, peerConnection);
                peerConnection.start();
            }
        }
        catch (IOException ex) {
            this.stopped = true;
        }
    }
}
