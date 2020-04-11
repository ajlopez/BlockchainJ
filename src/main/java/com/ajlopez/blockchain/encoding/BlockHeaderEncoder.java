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
        byte[] rlpTransactionsCount = RLPEncoder.encodeUnsignedInteger(blockHeader.getTransactionsCount());
        byte[] rlpTransactionsHash = RLPEncoder.encodeHash(blockHeader.getTransactionsRootHash());
        byte[] rlpStateRootHash = RLPEncoder.encodeHash(blockHeader.getStateRootHash());
        byte[] rlpTimestamp = RLPEncoder.encodeUnsignedLong(blockHeader.getTimestamp());
        byte[] rlpCoinbase = RLPEncoder.encodeAddress(blockHeader.getCoinbase());
        byte[] rlpDifficulty = RLPEncoder.encodeDifficulty(blockHeader.getDifficulty());

        return RLP.encodeList(rlpNumber, rlpParentHash, rlpTransactionsCount, rlpTransactionsHash, rlpStateRootHash, rlpTimestamp, rlpCoinbase, rlpDifficulty);
    }

    public static BlockHeader decode(byte[] encoded) {
        // TODO control number of elements
        byte[][] bytes = RLP.decodeList(encoded);

        long number = RLPEncoder.decodeUnsignedLong(bytes[0]);
        BlockHash parentHash = RLPEncoder.decodeBlockHash(bytes[1]);
        int transactionsCount = RLPEncoder.decodeUnsignedInteger(bytes[2]);
        Hash transactionsHash = RLPEncoder.decodeHash(bytes[3]);
        Hash stateRootHash = RLPEncoder.decodeHash(bytes[4]);
        long timestamp = RLPEncoder.decodeUnsignedLong(bytes[5]);
        Address coinbase = RLPEncoder.decodeAddress(bytes[6]);
        Difficulty difficulty = RLPEncoder.decodeDifficulty(bytes[7]);

        return new BlockHeader(number, parentHash, transactionsCount, transactionsHash, stateRootHash, timestamp, coinbase, difficulty);
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
