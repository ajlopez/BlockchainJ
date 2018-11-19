package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.OutputChannel;
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
public class OutputProcessor {
    private Map<PeerId, OutputChannel> channelsByPeer = new HashMap<>();

    public void connectToPeer(Peer receiver, OutputChannel channel) {
        channelsByPeer.put(receiver.getId(), channel);
    }

    public boolean isConnected(Peer receiver) {
        return channelsByPeer.containsKey(receiver.getId());
    }

    public void disconnectFromPeer(Peer receiver) {
        channelsByPeer.remove(receiver.getId());
    }

    public boolean postMessage(Peer receiver, Message message) {
        OutputChannel channel = channelsByPeer.get(receiver.getId());

        if (channel == null)
            return false;

        channel.postMessage(message);

        return true;
    }

    public int postMessage(Message message) {
        int sent = 0;

        Collection<OutputChannel> channels = this.channelsByPeer.values();

        for (OutputChannel channel: channels) {
            channel.postMessage(message);
            sent++;
        }

        return sent;
    }

    public int postMessage(Message message, List<PeerId> toSkip) {
        int sent = 0;

        for (Map.Entry<PeerId, OutputChannel> entry: this.channelsByPeer.entrySet()) {
            if (toSkip.contains(entry.getKey()))
                continue;

            entry.getValue().postMessage(message);

            sent++;
        }

        return sent;
    }
}
