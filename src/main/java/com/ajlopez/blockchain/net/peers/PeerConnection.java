package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.MessageChannel;
import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.messages.Message;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class PeerConnection implements MessageChannel {
    private final Peer sender;
    private final MessageInputStream messageInputStream;
    private final MessageOutputStream messageOutputStream;
    private final MessageChannel inputChannel;

    private Queue<Message> queue = new ConcurrentLinkedQueue<>();
    private boolean started;
    private boolean stopped;

    public PeerConnection(Peer sender, InputStream inputStream, OutputStream outputStream, MessageChannel inputChannel) {
        this.sender = sender;
        this.messageInputStream = new MessageInputStream(new PacketInputStream(inputStream));
        this.messageOutputStream = new MessageOutputStream(new PacketOutputStream(outputStream));
        this.inputChannel = inputChannel;
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
            while (!this.stopped)
                this.inputChannel.postMessage(this.sender, this.messageInputStream.readMessage());
        }
        catch (Exception ex) {
            this.stopped = true;
        }
    }

    private void writeProcess() {
        try {
            while (!this.stopped) {
                Message message = queue.poll();

                if (message != null)
                    this.messageOutputStream.writeMessage(message);
                else
                    Thread.sleep(100);
            }

            this.messageOutputStream.close();
        }
        catch (Exception ex) {
            this.stopped = true;
        }
    }

    public void postMessage(Peer peer, Message message) {
        if (!stopped)
            this.queue.add(message);
    }
}
