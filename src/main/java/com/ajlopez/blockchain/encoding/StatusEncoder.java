package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.utils.ByteUtils;

public class StatusEncoder {
    private StatusEncoder() {

    }

    public static byte[] encode(Status status) {
        byte[] rlpNodeId = RLPEncoder.encodePeerId(status.getPeerId());
        byte[] rlpNetworkNumber = RLPEncoder.encodeUnsignedLong(status.getNetworkNumber());
        byte[] rlpBestBlockNumber = RLPEncoder.encodeUnsignedLong(status.getBestBlockNumber());
        byte[] rlpBestBlockHash = RLPEncoder.encodeBlockHash(status.getBestBlockHash());

        return RLP.encodeList(rlpNodeId, rlpNetworkNumber, rlpBestBlockNumber, rlpBestBlockHash);
    }

    public static Status decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        PeerId nodeid = RLPEncoder.decodePeerId(bytes[0]);
        long networkNumber = RLPEncoder.decodeUnsignedLong(bytes[1]);
        long bestBlockNumber = RLPEncoder.decodeUnsignedLong(bytes[2]);
        BlockHash blockHash = RLPEncoder.decodeBlockHash(bytes[3]);

        return new Status(nodeid, networkNumber, bestBlockNumber, blockHash);
    }
}
