package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.MessageChannel;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.messages.Message;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 26/03/2018.
 */
public class SendProcessor {
    private final Peer sender;
    private Map<PeerId, MessageChannel> channelsByPeer = new HashMap<>();

    public SendProcessor(Peer sender) {
        this.sender = sender;
    }

    public void connectToPeer(Peer receiver, MessageChannel channel) {
        channelsByPeer.put(receiver.getId(), channel);
    }

    public boolean isConnected(Peer receiver) {
        return channelsByPeer.containsKey(receiver.getId());
    }

    public void disconnectFromPeer(Peer receiver) {
        channelsByPeer.remove(receiver.getId());
    }

    public boolean postMessage(Peer receiver, Message message) {
        MessageChannel channel = channelsByPeer.get(receiver.getId());

        if (channel == null)
            return false;

        channel.postMessage(this.sender, message);

        return true;
    }

    public int postMessage(Message message) {
        int sent = 0;

        Collection<MessageChannel> channels = this.channelsByPeer.values();

        for (MessageChannel channel: channels) {
            channel.postMessage(this.sender, message);
            sent++;
        }

        return sent;
    }

    public int postMessage(Message message, List<PeerId> toSkip) {
        int sent = 0;

        for (Map.Entry<PeerId, MessageChannel> entry: this.channelsByPeer.entrySet()) {
            if (toSkip.contains(entry.getKey()))
                continue;

            entry.getValue().postMessage(this.sender, message);

            sent++;
        }

        return sent;
    }
}
