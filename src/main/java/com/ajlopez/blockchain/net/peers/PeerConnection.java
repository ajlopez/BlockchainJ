package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.MessageChannel;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.Message;
import javafx.util.Pair;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class PeerConnection implements PeerNode {
    private final Peer peer;
    private final MessageInputStream messageInputStream;
    private final MessageOutputStream messageOutputStream;
    private final MessageChannel inputChannel;

    private Queue<Pair<Peer, Message>> queue = new ConcurrentLinkedQueue<>();
    private boolean started;
    private boolean stopped;

    public PeerConnection(short network, Peer peer, InputStream inputStream, OutputStream outputStream, MessageChannel inputChannel) {
        this.peer = peer;
        this.messageInputStream = new MessageInputStream(network, new PacketInputStream(inputStream));
        this.messageOutputStream = new MessageOutputStream(network, new PacketOutputStream(outputStream));
        this.inputChannel = inputChannel;
    }

    public Peer getPeer() {
        return this.peer;
    }

    public synchronized void start() {
        if (this.started)
            return;

        new Thread(this::readProcess).start();
        new Thread(this::writeProcess).start();

        this.started = true;
    }

    public void stop() {
        this.stopped = true;
    }

    private void readProcess() {
        try {
            while (!this.stopped) {
                Message message = this.messageInputStream.readMessage();

                if (message == null) {
                    this.stopped = true;
                    continue;
                }

                if (this.inputChannel != null)
                    this.inputChannel.postMessage(this.peer, message);
            }
        }
        catch (Exception ex) {
            this.stopped = true;
        }
    }

    private void writeProcess() {
        try {
            while (!this.stopped) {
                Pair<Peer, Message> peerMessage = queue.poll();

                if (this.stopped)
                    break;

                if (peerMessage != null) {
                    // TODO stopped if cannot write the message
                    if (!this.messageOutputStream.writeMessage(peerMessage.getKey(), peerMessage.getValue()))
                        stopped = true;
                }
                else
                    Thread.sleep(100);
            }

            this.messageOutputStream.close();
        }
        catch (Exception ex) {
            // TODO review, exception if message cannot be encoded
            this.stopped = true;
        }
    }

    public void postMessage(Peer peer, Message message) {
        if (!stopped)
            this.queue.add(new Pair(peer, message));
    }
}
