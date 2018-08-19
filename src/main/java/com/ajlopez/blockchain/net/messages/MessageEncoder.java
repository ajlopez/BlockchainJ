package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
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

        if (bytes[0] == MessageType.BLOCK.ordinal()) {
            Block block = BlockEncoder.decode(bbytes);

            return new BlockMessage(block);
        }

        if (bytes[0] == MessageType.GET_BLOCK_BY_NUMBER.ordinal()) {
            long number = ByteUtils.bytesToUnsignedLong(bbytes);

            return new GetBlockByNumberMessage(number);
        }

        if (bytes[0] == MessageType.GET_BLOCK_BY_HASH.ordinal()) {
            byte[] bhash = new byte[bytes.length - 1 - Integer.BYTES];
            System.arraycopy(bytes, 1 + Integer.BYTES, bhash, 0, bhash.length);
            Hash hash = new Hash(bhash);

            return new GetBlockByHashMessage(hash);
        }

        if (bytes[0] == MessageType.TRANSACTION.ordinal()) {
            byte[] btx = new byte[bytes.length - 1 - Integer.BYTES];
            System.arraycopy(bytes, 1 + Integer.BYTES, btx, 0, btx.length);
            Transaction tx = TransactionEncoder.decode(btx);

            return new TransactionMessage(tx);
        }x

        throw new UnsupportedOperationException();
    }
}
