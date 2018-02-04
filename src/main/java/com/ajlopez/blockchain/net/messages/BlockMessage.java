package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.encoding.BlockEncoder;

/**
 * Created by ajlopez on 19/01/2018.
 */
public class BlockMessage extends Message {
    public BlockMessage(Block block) {
        super(MessageType.BLOCK, BlockEncoder.encode(block));
    }

    public Block getBlock() {
        return BlockEncoder.decode(this.getPayload());
    }
}
