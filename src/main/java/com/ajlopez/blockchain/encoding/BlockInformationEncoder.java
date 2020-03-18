package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.bc.BlockInformation;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;

/**
 * Created by ajlopez on 17/03/2020.
 */
public class BlockInformationEncoder {
    private BlockInformationEncoder() {}

    public static byte[] encode(BlockInformation blockInformation) {
        byte[] rlpBlockHash = RLPEncoder.encodeBlockHash(blockInformation.getBlockHash());
        byte[] rlpTotalDifficulty = RLPEncoder.encodeDifficulty(blockInformation.getTotalDifficulty());

        return RLP.encodeList(rlpBlockHash, rlpTotalDifficulty);
    }

    public static BlockInformation decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        BlockHash blockHash = RLPEncoder.decodeBlockHash(bytes[0]);
        Difficulty totalDifficulty = RLPEncoder.decodeDifficulty(bytes[1]);

        return new BlockInformation(blockHash, totalDifficulty);
    }
}
