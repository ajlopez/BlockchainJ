package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.ByteUtils;

/**
 * Created by ajlopez on 23/09/2017.
 */
public class BlockHeaderEncoder {
    private BlockHeaderEncoder() {}

    public static byte[] encode(BlockHeader header) {
        byte[] rlpNumber = RLPEncoder.encodeUnsignedLong(header.getNumber());
        byte[] rlpParentHash = RLPEncoder.encodeBlockHash(header.getParentHash());
        byte[] rlpTransactionsHash = RLP.encode(header.getTransactionsHash().getBytes());
        byte[] rlpStateRootHash = RLP.encode(header.getStateRootHash().getBytes());
        byte[] rlpTimestamp = RLPEncoder.encodeUnsignedLong(header.getTimestamp());
        byte[] rlpCoinbase = RLPEncoder.encodeAddress(header.getCoinbase());

        return RLP.encodeList(rlpNumber, rlpParentHash, rlpTransactionsHash, rlpStateRootHash, rlpTimestamp, rlpCoinbase);
    }

    public static BlockHeader decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        long number = RLPEncoder.decodeUnsignedLong(bytes[0]);
        BlockHash parentHash = RLPEncoder.decodeBlockHash(bytes[1]);
        byte[] transactionsHash = RLP.decode(bytes[2]);
        byte[] stateRootHash = RLP.decode(bytes[3]);
        long timestamp = RLPEncoder.decodeUnsignedLong(bytes[4]);
        Address coinbase = RLPEncoder.decodeAddress(bytes[5]);

        return new BlockHeader(number, parentHash, new Hash(transactionsHash), new Hash(stateRootHash), timestamp, coinbase);
    }
}
