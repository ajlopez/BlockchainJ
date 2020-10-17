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
        byte[] rlpReceiptsHash = RLPEncoder.encodeHash(blockHeader.getReceiptsRootHash());
        byte[] rlpUnclesCount = RLPEncoder.encodeUnsignedInteger(blockHeader.getUnclesCount());
        byte[] rlpUnclesHash = RLPEncoder.encodeHash(blockHeader.getUnclesRootHash());
        byte[] rlpStateRootHash = RLPEncoder.encodeHash(blockHeader.getStateRootHash());
        byte[] rlpTimestamp = RLPEncoder.encodeUnsignedLong(blockHeader.getTimestamp());
        byte[] rlpCoinbase = RLPEncoder.encodeAddress(blockHeader.getCoinbase());
        byte[] rlpDifficulty = RLPEncoder.encodeDifficulty(blockHeader.getDifficulty());
        byte[] rlpGasLimit = RLPEncoder.encodeUnsignedLong(blockHeader.getGasLimit());
        byte[] rlpGasUsed = RLPEncoder.encodeUnsignedLong(blockHeader.getGasUsed());
        byte[] rlpNonce = RLPEncoder.encodeNonce(blockHeader.getNonce());

        return RLP.encodeList(rlpNumber, rlpParentHash, rlpTransactionsCount, rlpTransactionsHash, rlpReceiptsHash, rlpUnclesCount, rlpUnclesHash, rlpStateRootHash, rlpTimestamp, rlpCoinbase, rlpDifficulty, rlpGasLimit, rlpGasUsed, rlpNonce);
    }

    // TODO process gas limit
    public static BlockHeader decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        if (bytes.length != 14)
            throw new IllegalArgumentException("Invalid block header encoding");

        long number = RLPEncoder.decodeUnsignedLong(bytes[0]);
        BlockHash parentHash = RLPEncoder.decodeBlockHash(bytes[1]);
        int transactionsCount = RLPEncoder.decodeUnsignedInteger(bytes[2]);
        Hash transactionsHash = RLPEncoder.decodeHash(bytes[3]);
        Hash receiptsHash = RLPEncoder.decodeHash(bytes[4]);
        int unclesCount = RLPEncoder.decodeUnsignedInteger(bytes[5]);
        Hash unclesHash = RLPEncoder.decodeHash(bytes[6]);
        Hash stateRootHash = RLPEncoder.decodeHash(bytes[7]);
        long timestamp = RLPEncoder.decodeUnsignedLong(bytes[8]);
        Address coinbase = RLPEncoder.decodeAddress(bytes[9]);
        Difficulty difficulty = RLPEncoder.decodeDifficulty(bytes[10]);
        long gasLimit = RLPEncoder.decodeUnsignedLong(bytes[11]);
        long gasUsed = RLPEncoder.decodeUnsignedLong(bytes[12]);
        long nonce = RLPEncoder.decodeLong(bytes[13]);

        return new BlockHeader(number, parentHash, transactionsCount, transactionsHash, receiptsHash, unclesCount, unclesHash, stateRootHash, timestamp, coinbase, difficulty, gasLimit, gasUsed, null, nonce);
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
