package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Hash;
import com.ajlopez.blockchain.utils.ByteUtils;

/**
 * Created by ajlopez on 23/09/2017.
 */
public class BlockHeaderEncoder {
    private BlockHeaderEncoder() {}

    public static byte[] encode(BlockHeader header) {
        byte[] rlpNumber = RLP.encode(ByteUtils.longToBytes(header.getNumber()));
        byte[] rlpParentHash = RLP.encode(header.getParentHash().getBytes());

        return RLP.encodeList(rlpNumber, rlpParentHash);
    }

    public static BlockHeader decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        byte[] number = RLP.decode(bytes[0]);
        byte[] parentHash = RLP.decode(bytes[1]);

        return new BlockHeader(ByteUtils.bytesToLong(number), new Hash(parentHash));
    }
}
