package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Hash;
import com.ajlopez.blockchain.utils.ByteUtils;

/**
 * Created by ajlopez on 23/09/2017.
 */
public class BlockHeaderEncoder {
    public static byte[] encode(BlockHeader header) {
        byte[] rlpHash = RLP.encode(header.getParentHash().getBytes());
        byte[] rlpNumber = RLP.encode(ByteUtils.longToBytes(header.getNumber()));

        return RLP.encodeList(rlpNumber, rlpHash);
    }

    public static BlockHeader decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        byte[] number = RLP.decode(bytes[0]);
        byte[] hash = RLP.decode(bytes[1]);

        return new BlockHeader(ByteUtils.bytesToLong(number), new Hash(hash));
    }
}
