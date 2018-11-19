package com.ajlopez.blockchain.test.simples;

import com.ajlopez.blockchain.net.InputChannel;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.Message;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class SimpleInputChannel implements InputChannel {
    private List<Consumer<Pair<Peer, Message>>> consumers = new ArrayList<>();
    private List<Pair<Peer, Message>> peerMessages = new ArrayList<>();

    public void postMessage(Peer peer, Message message) {
        Pair<Peer, Message> peerMessage = new Pair(peer, message);
        this.peerMessages.add(peerMessage);
        this.consumers.forEach(consumer -> consumer.accept(peerMessage));
    }

    public void onMessage(Consumer<Pair<Peer, Message>> consumer) {
        this.consumers.add(consumer);
    }

    public List<Pair<Peer, Message>> getPeerMessages() {
        return this.peerMessages;
    }
}
