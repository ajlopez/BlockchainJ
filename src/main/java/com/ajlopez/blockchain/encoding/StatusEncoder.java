package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.Status;

public class StatusEncoder {
    private StatusEncoder() {

    }

    public static byte[] encode(Status status) {
        byte[] rlpNodeId = RLPEncoder.encodePeerId(status.getPeerId());
        byte[] rlpNetworkNumber = RLPEncoder.encodeUnsignedLong(status.getNetworkNumber());
        byte[] rlpBestBlockNumber = RLPEncoder.encodeLong(status.getBestBlockNumber());
        byte[] rlpBestBlockHash = RLPEncoder.encodeBlockHash(status.getBestBlockHash());
        byte[] rlpBestTotalDifficulty = RLPEncoder.encodeDifficulty(status.getBestTotalDifficulty());

        return RLP.encodeList(rlpNodeId, rlpNetworkNumber, rlpBestBlockNumber, rlpBestBlockHash, rlpBestTotalDifficulty);
    }

    public static Status decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        if (bytes.length != 5)
            throw new IllegalArgumentException("Invalid status encoding");

        PeerId nodeid = RLPEncoder.decodePeerId(bytes[0]);
        long networkNumber = RLPEncoder.decodeUnsignedLong(bytes[1]);
        long bestBlockNumber = RLPEncoder.decodeLong(bytes[2]);
        BlockHash bestBlockHash = RLPEncoder.decodeBlockHash(bytes[3]);
        Difficulty bestTotalDifficulty = RLPEncoder.decodeDifficulty(bytes[4]);

        return new Status(nodeid, networkNumber, bestBlockNumber, bestBlockHash, bestTotalDifficulty);
    }
}
