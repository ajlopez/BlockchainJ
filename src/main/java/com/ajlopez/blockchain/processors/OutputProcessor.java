package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.messages.Message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 26/03/2018.
 */
public class OutputProcessor {
    private Map<PeerId, OutputChannel> channelsByPeer = new HashMap<>();

    public void registerPeer(Peer peer, OutputChannel channel) {
        channelsByPeer.put(peer.getId(), channel);
    }

    public boolean postMessage(Peer peer, Message message) {
        OutputChannel channel = channelsByPeer.get(peer.getId());

        if (channel == null)
            return false;

        channel.postMessage(message);

        return true;
    }

    public boolean postMessage(Message message) {
        Collection<OutputChannel> channels = this.channelsByPeer.values();

        for (OutputChannel channel: channels)
            channel.postMessage(message);

        return !channels.isEmpty();
    }
}
