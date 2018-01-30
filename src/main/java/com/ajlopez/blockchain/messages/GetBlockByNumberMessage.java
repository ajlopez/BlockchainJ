package com.ajlopez.blockchain.messages;

import com.ajlopez.blockchain.core.Hash;
import com.ajlopez.blockchain.utils.ByteUtils;

/**
 * Created by ajlopez on 30/01/2018.
 */
public class GetBlockByNumberMessage extends Message {
    public GetBlockByNumberMessage(long number) {
        super(MessageType.GET_BLOCK_BY_NUMBER, ByteUtils.unsignedLongToBytes(number));
    }

    public long getNumber() {
        return ByteUtils.bytesToUnsignedLong(this.getPayload());
    }
}
