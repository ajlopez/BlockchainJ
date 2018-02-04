package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.messages.Message;

/**
 * Created by ajlopez on 02/02/2018.
 */
public interface OutputChannel {
    void postMessage(Message message);
}
