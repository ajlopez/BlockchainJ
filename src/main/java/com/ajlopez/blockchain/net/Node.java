package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.core.types.Hash;

/**
 * Created by ajlopez on 04/02/2018.
 */
public class Node {
    private Hash hash;

    public Node(Hash hash) {
        this.hash = hash;
    }

    public Hash getHash() {
        return this.hash;
    }
}
