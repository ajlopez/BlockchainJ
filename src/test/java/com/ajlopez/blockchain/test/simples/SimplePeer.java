package com.ajlopez.blockchain.test.simples;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 12/02/2018.
 */
public class SimplePeer extends Peer {
    public SimplePeer(PeerId id) {
        super(id);
    }
}
