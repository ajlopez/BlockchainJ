package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.net.InputChannel;
import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.processors.InputProcessor;

public class PeerToPeerOutputChannel implements OutputChannel {
    private Peer fromPeer;
    private Peer toPeer;
    private InputChannel inputChannel;

    public PeerToPeerOutputChannel(Peer fromPeer, Peer toPeer, InputChannel inputChannel) {
        this.fromPeer = fromPeer;
        this.toPeer = toPeer;
        this.inputChannel = inputChannel;
    }

    @Override
    public void postMessage(Message message) {
        this.inputChannel.postMessage(this.fromPeer, message);
    }
}