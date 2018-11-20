package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.net.MessageChannel;
import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.messages.Message;

public class PeerToPeerOutputChannel implements OutputChannel {
    private Peer fromPeer;
    private MessageChannel inputChannel;

    public PeerToPeerOutputChannel(Peer fromPeer, MessageChannel inputChannel) {
        this.fromPeer = fromPeer;
        this.inputChannel = inputChannel;
    }

    @Override
    public void postMessage(Message message) {
        this.inputChannel.postMessage(this.fromPeer, message);
    }
}
