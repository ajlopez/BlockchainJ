package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.*;

import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class MessageProcessor {
    BlockProcessor blockProcessor;
    TransactionProcessor transactionProcessor;
    PeerProcessor peerProcessor;
    OutputProcessor outputProcessor;

    public MessageProcessor(BlockProcessor blockProcessor, TransactionProcessor transactionProcessor, PeerProcessor peerProcessor, OutputProcessor outputProcessor) {
        this.blockProcessor = blockProcessor;
        this.transactionProcessor = transactionProcessor;
        this.peerProcessor = peerProcessor;
        this.outputProcessor = outputProcessor;
    }

    public void processMessage(Message message, Peer sender) {
        MessageType msgtype = message.getMessageType();

        if (msgtype == MessageType.BLOCK)
            this.processBlockMessage((BlockMessage)message, sender);
        else if (msgtype == MessageType.GET_BLOCK_BY_HASH)
            this.processGetBlockByHashMessage((GetBlockByHashMessage) message, sender);
        else if (msgtype == MessageType.GET_BLOCK_BY_NUMBER)
            this.processGetBlockByNumberMessage((GetBlockByNumberMessage) message, sender);
        else if (msgtype == MessageType.TRANSACTION)
            this.transactionProcessor.processTransaction(((TransactionMessage)message).getTransaction());
        else if (msgtype == MessageType.STATUS)
            this.processStatusMessage((StatusMessage)message, sender);
    }

    private void processBlockMessage(BlockMessage message, Peer sender) {
        List<Block> processed = this.blockProcessor.processBlock(message.getBlock());

        if (this.outputProcessor == null)
            return;

        int nprocessed = 0;

        for (Block block : processed) {
            Message outputMessage = new BlockMessage(block);

            if (nprocessed == 0 && sender != null)
                this.outputProcessor.postMessage(outputMessage, Collections.singletonList(sender.getId()));
            else
                this.outputProcessor.postMessage(outputMessage);

            nprocessed++;
        }
    }

    private void processStatusMessage(StatusMessage message, Peer sender) {
        Hash senderId = sender.getId();

        long peerNumber = this.peerProcessor.getPeerBestBlockNumber(senderId);

        this.peerProcessor.registerBestBlockNumber(senderId, message.getStatus().getBestBlockNumber());

        long fromNumber = this.blockProcessor.getBestBlockNumber();

        if (fromNumber < peerNumber)
            fromNumber = peerNumber;

        long toNumber = this.peerProcessor.getPeerBestBlockNumber(senderId);

        for (long number = fromNumber + 1; number <= toNumber; number++)
            outputProcessor.postMessage(sender, new GetBlockByNumberMessage(number));
    }

    private void processGetBlockByHashMessage(GetBlockByHashMessage message, Peer sender) {
        Block block = this.blockProcessor.getBlockByHash(message.getHash());

        if (block != null)
            outputProcessor.postMessage(sender, new BlockMessage(block));
    }

    private void processGetBlockByNumberMessage(GetBlockByNumberMessage message, Peer sender) {
        Block block = this.blockProcessor.getBlockByNumber(message.getNumber());

        if (block != null)
            outputProcessor.postMessage(sender, new BlockMessage(block));
    }
}
