package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.utils.ByteUtils;

public class MessageEncoder {
    private MessageEncoder() {

    }

    public static byte[] encode(Message message) {
        byte[] payload = message.getPayload();
        int plength = payload.length;

        byte[] bytes = new byte[1 + Integer.BYTES + plength];

        bytes[0] = (byte)message.getMessageType().ordinal();
        ByteUtils.unsignedIntegerToBytes(plength, bytes, 1);

        System.arraycopy(payload, 0, bytes, 1 + Integer.BYTES, plength);

        return bytes;
    }

    public static Message decode(byte[] bytes) {
        byte[] bbytes = new byte[bytes.length - 1 - Integer.BYTES];
        System.arraycopy(bytes, 1 + Integer.BYTES, bbytes, 0, bbytes.length);

        Block block = BlockEncoder.decode(bbytes);

        return new BlockMessage(block);
    }
}
