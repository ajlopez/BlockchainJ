package com.ajlopez.blockchain.test.utils;

import com.ajlopez.blockchain.net.peers.PeerConnection;
import com.ajlopez.blockchain.processors.NodeProcessor;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class NodesHelper {
    private NodesHelper() {

    }

    public static void runNodeProcessors(NodeProcessor...nodeProcessors) throws InterruptedException {
        List<Semaphore> semaphores = new ArrayList<>();

        for (NodeProcessor nodeProcessor : nodeProcessors) {
            Semaphore semaphore = new Semaphore(0, true);

            nodeProcessor.onEmpty(() -> {
                semaphore.release();
            });

            semaphores.add(semaphore);
        }

        for (NodeProcessor nodeProcessor : nodeProcessors)
            nodeProcessor.startMessageProcessing();

        for (Semaphore semaphore : semaphores)
            semaphore.acquire();

        for (NodeProcessor nodeProcessor : nodeProcessors)
            nodeProcessor.stopMessageProcessing();
    }

    public static List<PeerConnection> connectNodeProcessors(NodeProcessor ...nodeProcessors) throws InterruptedException, IOException {
        List<PeerConnection> connections = new ArrayList<>();
        int nnodes = nodeProcessors.length;

        for (int k = 0; k < nnodes; k++)
            for (int j = k + 1; j < nnodes; j++) {
                connections.addAll(connectNodes(nodeProcessors[k], nodeProcessors[j]));
            }

        return connections;
    }

    public static List<PeerConnection> connectNodes(NodeProcessor node1, NodeProcessor node2) throws IOException {
        PipedOutputStream outputStream1 = new PipedOutputStream();
        PipedInputStream inputStream1 = new PipedInputStream();
        inputStream1.connect(outputStream1);

        PipedOutputStream outputStream2 = new PipedOutputStream();
        PipedInputStream inputStream2 = new PipedInputStream();
        inputStream2.connect(outputStream2);

        PeerConnection connection1 = new PeerConnection(node1.getPeer(), inputStream1, outputStream2, node2);
        PeerConnection connection2 = new PeerConnection(node2.getPeer(), inputStream2, outputStream1, node1);

        List<PeerConnection> connections = new ArrayList<>();
        connections.add(connection1);
        connections.add(connection2);

        node1.connectTo(connection2);
        node2.connectTo(connection1);

        return connections;
    }
}
