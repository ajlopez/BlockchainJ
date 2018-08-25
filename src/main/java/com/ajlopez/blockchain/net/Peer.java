package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.messages.Message;

/**
 * Created by ajlopez on 04/02/2018.
 */
public class Peer {
    private PeerId id;

    public Peer(PeerId id) {
        this.id = id;
    }

    public PeerId getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof Peer))
            return false;

        Peer peer = (Peer)obj;

        return this.getId().equals(peer.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
