package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Transaction;

import java.util.ArrayList;
import java.util.List;

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

        byte[][] encodedtxs = RLP.decodeList(bytes[1]);

        List<Transaction> txs = new ArrayList<>();

        for (int k = 0; k < encodedtxs.length; k++)
            txs.add(TransactionEncoder.decode(encodedtxs[k]));

        return new Block(header, txs);
    }
}
