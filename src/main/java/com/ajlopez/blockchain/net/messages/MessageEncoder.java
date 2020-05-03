package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.encoding.RLP;
import com.ajlopez.blockchain.encoding.StatusEncoder;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.store.StoreType;
import com.ajlopez.blockchain.store.TrieType;
import com.ajlopez.blockchain.utils.ByteUtils;

public class MessageEncoder {
    private MessageEncoder() {

    }

    public static byte[] encode(Message message) {
        byte[] payload = message.getPayload();

        if (payload == null) {
            byte[] bytes = new byte[1];

            bytes[0] = (byte)message.getMessageType().ordinal();

            return bytes;
        }

        int plength = payload.length;

        byte[] bytes = new byte[1 + Integer.BYTES + plength];

        bytes[0] = (byte)message.getMessageType().ordinal();
        ByteUtils.unsignedIntegerToBytes(plength, bytes, 1);

        System.arraycopy(payload, 0, bytes, 1 + Integer.BYTES, plength);

        return bytes;
    }

    public static Message decode(byte[] bytes) {
        if (bytes[0] == MessageType.GET_STATUS.ordinal())
            return GetStatusMessage.getInstance();

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
            BlockHash hash = new BlockHash(bhash);

            return new GetBlockByHashMessage(hash);
        }

        if (bytes[0] == MessageType.TRANSACTION.ordinal()) {
            byte[] btx = new byte[bytes.length - 1 - Integer.BYTES];
            System.arraycopy(bytes, 1 + Integer.BYTES, btx, 0, btx.length);
            Transaction tx = TransactionEncoder.decode(btx);

            return new TransactionMessage(tx);
        }

        if (bytes[0] == MessageType.STATUS.ordinal()) {
            byte[] bstatus = new byte[bytes.length - 1 - Integer.BYTES];
            System.arraycopy(bytes, 1 + Integer.BYTES, bstatus, 0, bstatus.length);
            Status status = StatusEncoder.decode(bstatus);

            return new StatusMessage(status);
        }

        if (bytes[0] == MessageType.TRIE_NODE.ordinal()) {
            byte[][] lbytes = RLP.decodeList(bbytes);
            byte[] bhash = RLP.decode(lbytes[0]);
            byte[] btype = RLP.decode(lbytes[1]);
            byte[] data = RLP.decode(lbytes[2]);
            TrieType trieType = TrieType.values()[btype[0]];

            return new TrieNodeMessage(new Hash(bhash), trieType, data);
        }

        if (bytes[0] == MessageType.GET_TRIE_NODE.ordinal()) {
            byte[][] lbytes = RLP.decodeList(bbytes);
            byte[] btophash = RLP.decode(lbytes[0]);
            byte[] btype = RLP.decode(lbytes[1]);
            byte[] bhash = RLP.decode(lbytes[2]);
            TrieType trieType = TrieType.values()[btype[0]];

            return new GetTrieNodeMessage(new Hash(btophash), trieType, new Hash(bhash));
        }

        if (bytes[0] == MessageType.GET_STORED_VALUE.ordinal()) {
            byte[][] lbytes = RLP.decodeList(bbytes);
            byte[] btype = RLP.decode(lbytes[0]);
            byte[] bkey = RLP.decode(lbytes[1]);
            StoreType storeType = StoreType.values()[btype[0]];

            return new GetStoredValueMessage(storeType, bkey);
        }

        if (bytes[0] == MessageType.STORED_KEY_VALUE.ordinal()) {
            byte[][] lbytes = RLP.decodeList(bbytes);
            byte[] btype = RLP.decode(lbytes[0]);
            byte[] bkey = RLP.decode(lbytes[1]);
            byte[] bvalue = RLP.decode(lbytes[2]);
            StoreType storeType = StoreType.values()[btype[0]];

            return new StoredKeyValueMessage(storeType, bkey, bvalue);
        }

        if (bytes[0] == MessageType.GET_BLOCK_HASHES.ordinal()) {
            byte[][] lbytes = RLP.decodeList(bbytes);
            byte[] bheight = RLP.decode(lbytes[0]);
            byte[] bno = RLP.decode(lbytes[1]);
            byte[] bgap = RLP.decode(lbytes[2]);

            return new GetBlockHashesMessage(ByteUtils.bytesToUnsignedLong(bheight), ByteUtils.bytesToUnsignedInteger(bno), ByteUtils.bytesToUnsignedInteger(bgap));
        }

        throw new UnsupportedOperationException();
    }
}
