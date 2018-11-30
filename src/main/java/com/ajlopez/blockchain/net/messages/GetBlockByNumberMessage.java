package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.utils.ByteUtils;

/**
 * Created by ajlopez on 30/01/2018.
 */
public class GetBlockByNumberMessage extends Message {
    private long number;

    public GetBlockByNumberMessage(long number) {
        super(MessageType.GET_BLOCK_BY_NUMBER);
        this.number = number;
    }

    public long getNumber() {
        return ByteUtils.bytesToUnsignedLong(this.getPayload());
    }

    @Override
    public byte[] getPayload() {
        return ByteUtils.unsignedLongToNormalizedBytes(this.number);
    }
}
