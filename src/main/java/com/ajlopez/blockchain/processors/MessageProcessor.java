package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.messages.BlockMessage;
import com.ajlopez.blockchain.messages.Message;
import com.ajlopez.blockchain.messages.MessageType;
import com.ajlopez.blockchain.messages.TransactionMessage;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class MessageProcessor {
    BlockProcessor blockProcessor;
    TransactionProcessor transactionProcessor;

    public MessageProcessor(BlockProcessor blockProcessor, TransactionProcessor transactionProcessor) {
        this.blockProcessor = blockProcessor;
        this.transactionProcessor = transactionProcessor;
    }

    public void processMessage(Message message) {
        MessageType msgtype = message.getMessageType();

        if (msgtype == MessageType.BLOCK)
            this.blockProcessor.processBlock(((BlockMessage)message).getBlock());
        else if (msgtype == MessageType.BLOCK.TRANSACTION)
            this.transactionProcessor.processTransaction(((TransactionMessage)message).getTransaction());
    }
}
