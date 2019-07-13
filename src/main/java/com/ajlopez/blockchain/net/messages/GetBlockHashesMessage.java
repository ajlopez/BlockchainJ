package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.encoding.RLP;
import com.ajlopez.blockchain.utils.ByteUtils;

/**
 * Created by ajlopez on 12/07/2019.
 */
public class GetBlockHashesMessage extends Message {
    private final long blockHeight;
    private final int noBlocks;
    private final int blockGap;

    public GetBlockHashesMessage(long blockHeight, int noBlocks, int blockGap) {
        super(MessageType.GET_BLOCK_HASHES);
        this.blockHeight = blockHeight;
        this.noBlocks = noBlocks;
        this.blockGap = blockGap;
    }

    public long getBlockHeight() {
        return this.blockHeight;
    }

    public int getNoBlocks() {
        return this.noBlocks;
    }

    public int getBlockGap() {
        return this.blockGap;
    }

    @Override
    public byte[] getPayload() {
        byte[] bheight = ByteUtils.unsignedLongToNormalizedBytes(this.blockHeight);
        byte[] bno = ByteUtils.unsignedIntegerToNormalizedBytes(this.noBlocks);
        byte[] bgap = ByteUtils.unsignedIntegerToNormalizedBytes(this.blockGap);

        return RLP.encodeList(RLP.encode(bheight), RLP.encode(bno), RLP.encode(bgap));
    }
}

