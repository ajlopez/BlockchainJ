package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.net.messages.Message;

/**
 * Created by ajlopez on 02/02/2018.
 */
public interface InputChannel {
    void postMessage(Message message, Node sender);
}
