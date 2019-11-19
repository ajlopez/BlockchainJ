package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 23/09/2017.
 */
public class BlockHeaderEncoder {
    private BlockHeaderEncoder() {}

    public static byte[] encode(BlockHeader blockHeader) {
        byte[] rlpNumber = RLPEncoder.encodeUnsignedLong(blockHeader.getNumber());
        byte[] rlpParentHash = RLPEncoder.encodeBlockHash(blockHeader.getParentHash());
        byte[] rlpTransactionsHash = RLPEncoder.encodeHash(blockHeader.getTransactionsRootHash());
        byte[] rlpStateRootHash = RLPEncoder.encodeHash(blockHeader.getStateRootHash());
        byte[] rlpTimestamp = RLPEncoder.encodeUnsignedLong(blockHeader.getTimestamp());
        byte[] rlpCoinbase = RLPEncoder.encodeAddress(blockHeader.getCoinbase());
        byte[] rlpDifficulty = RLPEncoder.encodeDifficulty(blockHeader.getDifficulty());

        return RLP.encodeList(rlpNumber, rlpParentHash, rlpTransactionsHash, rlpStateRootHash, rlpTimestamp, rlpCoinbase, rlpDifficulty);
    }

    public static BlockHeader decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        long number = RLPEncoder.decodeUnsignedLong(bytes[0]);
        BlockHash parentHash = RLPEncoder.decodeBlockHash(bytes[1]);
        Hash transactionsHash = RLPEncoder.decodeHash(bytes[2]);
        Hash stateRootHash = RLPEncoder.decodeHash(bytes[3]);
        long timestamp = RLPEncoder.decodeUnsignedLong(bytes[4]);
        Address coinbase = RLPEncoder.decodeAddress(bytes[5]);
        Difficulty difficulty = RLPEncoder.decodeDifficulty(bytes[6]);

        return new BlockHeader(number, parentHash, transactionsHash, stateRootHash, timestamp, coinbase, difficulty);
    }

    public static byte[] encode(List<BlockHeader> blockHeaders) {
        byte[][] rlpBlockHeaders = new byte[blockHeaders.size()][];

        for (int k = 0; k < rlpBlockHeaders.length; k++)
            rlpBlockHeaders[k] = encode(blockHeaders.get(k));

        return RLP.encodeList(rlpBlockHeaders);
    }

    public static List<BlockHeader> decodeList(byte[] encoded) {
        byte[][] encodedBlockHeaders = RLP.decodeList(encoded);

        List<BlockHeader> blockHeaders = new ArrayList<>();

        for (int k = 0; k < encodedBlockHeaders.length; k++)
            blockHeaders.add(decode(encodedBlockHeaders[k]));

        return blockHeaders;
    }
}
