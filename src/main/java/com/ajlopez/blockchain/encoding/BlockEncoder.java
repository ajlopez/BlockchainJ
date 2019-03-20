package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;

/**
 * Created by ajlopez on 10/10/2017.
 */
public class BlockEncoder {
    private BlockEncoder() {}

    public static byte[] encode(Block block) {
        byte[] rlpHeader = BlockHeaderEncoder.encode(block.getHeader());

        return RLP.encodeList(rlpHeader, TransactionEncoder.encode(block.getTransactions()));
    }

    public static Block decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        BlockHeader header = BlockHeaderEncoder.decode(bytes[0]);

        return new Block(header, TransactionEncoder.decodeList(bytes[1]));
    }
}
