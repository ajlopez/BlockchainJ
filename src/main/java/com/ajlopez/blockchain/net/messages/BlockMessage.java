package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.encoding.BlockEncoder;

/**
 * Created by ajlopez on 19/01/2018.
 */
public class BlockMessage extends Message {
    private Block block;

    public BlockMessage(Block block) {
        super(MessageType.BLOCK);
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public byte[] getPayload() {
        return BlockEncoder.encode(this.block);
    }
}
