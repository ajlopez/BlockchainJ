package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.messages.BlockMessage;
import com.ajlopez.blockchain.messages.Message;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class MessageProcessor {
    BlockProcessor blockProcessor;

    public MessageProcessor(BlockProcessor blockProcessor) {
        this.blockProcessor = blockProcessor;
    }

    public void processMessage(Message message) {
        BlockMessage blockMessage = (BlockMessage)message;

        this.blockProcessor.processBlock(blockMessage.getBlock());
    }
}
