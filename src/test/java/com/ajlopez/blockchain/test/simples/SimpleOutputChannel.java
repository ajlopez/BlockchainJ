package com.ajlopez.blockchain.test.simples;

import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 07/02/2018.
 */
public class SimpleOutputChannel implements OutputChannel {
    private List<Message> messages = new ArrayList<>();

    @Override
    public void postMessage(Message message) {
        this.messages.add(message);
    }

    public Message getMessage() {
        if (this.messages.isEmpty())
            return null;

        return this.messages.get(this.messages.size() - 1);
    }

    public List<Message> getMessages() {
        return this.messages;
    }
}
