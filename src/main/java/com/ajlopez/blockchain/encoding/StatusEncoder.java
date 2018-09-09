package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.utils.ByteUtils;

public class StatusEncoder {
    private StatusEncoder() {

    }

    public static byte[] encode(Status status) {
        byte[] rlpNodeId = RLP.encode(status.getNodeId().getBytes());
        byte[] rlpNetworkNumber = RLP.encode(ByteUtils.unsignedLongToBytes(status.getNetworkNumber()));
        byte[] rlpBestBlockNumber = RLP.encode(ByteUtils.unsignedLongToBytes(status.getBestBlockNumber()));

        return RLP.encodeList(rlpNodeId, rlpNetworkNumber, rlpBestBlockNumber);
    }

    public static Status decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        Hash nodeid = new Hash(RLP.decode(bytes[0]));
        long networkNumber = ByteUtils.bytesToUnsignedLong(RLP.decode(bytes[1]));
        long bestBlockNumber = ByteUtils.bytesToUnsignedLong(RLP.decode(bytes[2]));

        return new Status(nodeid, networkNumber, bestBlockNumber);
    }
}