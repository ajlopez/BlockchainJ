package com.ajlopez.blockchain.test.simples;

import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.messages.Message;
import org.junit.Test;

/**
 * Created by ajlopez on 07/02/2018.
 */
public class SimpleOutputChannel implements OutputChannel {
    private Message message;

    @Override
    public void postMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }
}
