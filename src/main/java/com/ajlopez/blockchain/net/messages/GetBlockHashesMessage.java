package com.ajlopez.blockchain.net.messages;

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
        return null;
    }
}

