package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.utils.ByteUtils;

public class StatusEncoder {
    private StatusEncoder() {

    }

    public static byte[] encode(Status status) {
        byte[] rlpNodeId = RLP.encode(status.getPeerId().getBytes());
        byte[] rlpNetworkNumber = RLP.encode(ByteUtils.unsignedLongToBytes(status.getNetworkNumber()));
        byte[] rlpBestBlockNumber = RLP.encode(ByteUtils.unsignedLongToBytes(status.getBestBlockNumber()));
        byte[] rlpBestBlockHash = RLP.encode(status.getBestBlockHash().getBytes());

        return RLP.encodeList(rlpNodeId, rlpNetworkNumber, rlpBestBlockNumber, rlpBestBlockHash);
    }

    public static Status decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        PeerId nodeid = new PeerId(RLP.decode(bytes[0]));
        long networkNumber = ByteUtils.bytesToUnsignedLong(RLP.decode(bytes[1]));
        long bestBlockNumber = ByteUtils.bytesToUnsignedLong(RLP.decode(bytes[2]));
        BlockHash blockHash = new BlockHash(RLP.decode(bytes[3]));

        return new Status(nodeid, networkNumber, bestBlockNumber, blockHash);
    }
}
