package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.processors.InputProcessor;

public class PeerToPeerOutputChannel implements OutputChannel {
    private Peer fromPeer;
    private Peer toPeer;
    private InputProcessor inputProcessor;

    public PeerToPeerOutputChannel(Peer fromPeer, Peer toPeer, InputProcessor inputProcessor) {
        this.fromPeer = fromPeer;
        this.toPeer = toPeer;
        this.inputProcessor = inputProcessor;
    }

    @Override
    public void postMessage(Message message) {
        this.inputProcessor.postMessage(this.fromPeer, message);
    }
}
