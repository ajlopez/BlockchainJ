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
        byte[] rlpNumber = RLP.encode(ByteUtils.unsignedLongToNormalizedBytes(header.getNumber()));
        byte[] rlpParentHash = RLP.encode(header.getParentHash().getBytes());
        byte[] rlpTransactionsHash = RLP.encode(header.getTransactionsHash().getBytes());
        byte[] rlpStateRootHash = RLP.encode(header.getStateRootHash().getBytes());
        byte[] rlpTimestamp = RLP.encode(ByteUtils.unsignedLongToNormalizedBytes(header.getTimestamp()));
        byte[] rlpCoinbase = RLP.encode(header.getCoinbase().getBytes());

        return RLP.encodeList(rlpNumber, rlpParentHash, rlpTransactionsHash, rlpStateRootHash, rlpTimestamp, rlpCoinbase);
    }

    public static BlockHeader decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        byte[] number = RLP.decode(bytes[0]);
        byte[] parentHash = RLP.decode(bytes[1]);
        byte[] transactionsHash = RLP.decode(bytes[2]);
        byte[] stateRootHash = RLP.decode(bytes[3]);
        byte[] timestamp = RLP.decode(bytes[4]);
        byte[] coinbase = RLP.decode(bytes[5]);

        return new BlockHeader(ByteUtils.bytesToUnsignedLong(number), new BlockHash(parentHash), new Hash(transactionsHash), new Hash(stateRootHash), ByteUtils.bytesToUnsignedLong(timestamp), new Address(coinbase));
    }
}
