package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.encoding.StatusEncoder;
import com.ajlopez.blockchain.net.Status;

/**
 * Created by ajlopez on 01/05/2020.
 */
public class GetStatusMessage extends Message {
    private static GetStatusMessage instance = new GetStatusMessage();

    public static GetStatusMessage getInstance() {
        return instance;
    }

    private GetStatusMessage() {
        super(MessageType.GET_STATUS);
    }

    @Override
    public byte[] getPayload() {
        return null;
    }
}
