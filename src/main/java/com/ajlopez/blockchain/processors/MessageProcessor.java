package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.*;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class MessageProcessor {
    BlockProcessor blockProcessor;
    TransactionProcessor transactionProcessor;
    PeerProcessor peerProcessor;

    public MessageProcessor(BlockProcessor blockProcessor, TransactionProcessor transactionProcessor, PeerProcessor peerProcessor) {
        this.blockProcessor = blockProcessor;
        this.transactionProcessor = transactionProcessor;
        this.peerProcessor = peerProcessor;
    }

    public void processMessage(Message message, Peer sender) {
        MessageType msgtype = message.getMessageType();

        if (msgtype == MessageType.BLOCK)
            this.blockProcessor.processBlock(((BlockMessage)message).getBlock());
        else if (msgtype == MessageType.GET_BLOCK_BY_HASH)
            this.processGetBlockByHashMessage((GetBlockByHashMessage) message, sender);
        else if (msgtype == MessageType.GET_BLOCK_BY_NUMBER)
            this.processGetBlockByNumberMessage((GetBlockByNumberMessage) message, sender);
        else if (msgtype == MessageType.BLOCK.TRANSACTION)
            this.transactionProcessor.processTransaction(((TransactionMessage)message).getTransaction());
        else if (msgtype == MessageType.STATUS)
            this.processStatusMessage((StatusMessage)message, sender);
    }

    private void processStatusMessage(StatusMessage message, Peer sender) {
        this.peerProcessor.registerBestBlockNumber(sender.getHash(), message.getBestBlockNumber());
    }

    private void processGetBlockByHashMessage(GetBlockByHashMessage message, OutputChannel channel) {
        Block block = this.blockProcessor.getBlockByHash(message.getHash());

        if (block != null)
            channel.postMessage(new BlockMessage(block));
    }

    private void processGetBlockByNumberMessage(GetBlockByNumberMessage message, OutputChannel channel) {
        Block block = this.blockProcessor.getBlockByNumber(message.getNumber());

        if (block != null)
            channel.postMessage(new BlockMessage(block));
    }
}
