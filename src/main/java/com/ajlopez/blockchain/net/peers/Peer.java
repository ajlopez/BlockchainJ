package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.PeerId;

import java.util.Random;

/**
 * Created by ajlopez on 04/02/2018.
 */
public class Peer {
    private static final Random random = new Random();

    private final PeerId id;

    public static Peer createRandomPeer() {
        byte[] hashBytes = new byte[Hash.HASH_BYTES];
        random.nextBytes(hashBytes);
        return new Peer(new PeerId(hashBytes));
    }

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
