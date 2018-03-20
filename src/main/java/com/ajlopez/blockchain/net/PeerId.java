package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.core.types.Hash;

/**
 * Created by ajlopez on 20/03/2018.
 */
public class PeerId extends Hash {
    public PeerId(byte[] bytes) {
        super(bytes);
    }
}

