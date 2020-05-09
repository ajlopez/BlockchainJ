package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.messages.*;
import com.ajlopez.blockchain.store.KeyValueStores;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class MessageProcessor {
    private final Peer peer;
    private final NetworkConfiguration networkConfiguration;
    private final BlockProcessor blockProcessor;
    private final TransactionProcessor transactionProcessor;
    private final PeerProcessor peerProcessor;
    private final SendProcessor outputProcessor;
    private final WarpProcessor warpProcessor;
    private final KeyValueStores keyValueStores;
    private final KeyValueProcessor keyValueProcessor;

    public MessageProcessor(Peer peer, NetworkConfiguration networkConfiguration, BlockProcessor blockProcessor, TransactionProcessor transactionProcessor, PeerProcessor peerProcessor, SendProcessor outputProcessor, WarpProcessor warpProcessor, KeyValueStores keyValueStores, KeyValueProcessor keyValueProcessor) {
        this.peer = peer;
        this.networkConfiguration = networkConfiguration;
        this.blockProcessor = blockProcessor;
        this.transactionProcessor = transactionProcessor;
        this.peerProcessor = peerProcessor;
        this.outputProcessor = outputProcessor;
        this.warpProcessor = warpProcessor;
        this.keyValueStores = keyValueStores;
        this.keyValueProcessor = keyValueProcessor;
    }

    public void processMessage(Message message, Peer sender) {
        MessageType msgtype = message.getMessageType();

        try {
            if (msgtype == MessageType.BLOCK)
                this.processBlockMessage((BlockMessage) message, sender);
            else if (msgtype == MessageType.GET_BLOCK_BY_HASH)
                this.processGetBlockByHashMessage((GetBlockByHashMessage) message, sender);
            else if (msgtype == MessageType.GET_BLOCK_BY_NUMBER)
                this.processGetBlockByNumberMessage((GetBlockByNumberMessage) message, sender);
            else if (msgtype == MessageType.TRANSACTION)
                this.processTransactionMessage((TransactionMessage) message, sender);
            else if (msgtype == MessageType.STATUS)
                this.processStatusMessage((StatusMessage) message, sender);
            else if (msgtype == MessageType.TRIE_NODE)
                this.processTrieNodeMessage((TrieNodeMessage) message);
            else if (msgtype == MessageType.GET_STATUS)
                this.processGetStatusMessage(sender);
            else if (msgtype == MessageType.GET_STORED_VALUE)
                this.processGetStoredValueMessage((GetStoredValueMessage) message, sender);
            else if (msgtype == MessageType.STORED_KEY_VALUE)
                this.processStoredKeyValueMessage((StoredKeyValueMessage) message);
        }
        catch (IOException ex) {
            // Add to logger
            ex.printStackTrace();
        }
    }

    private void processStoredKeyValueMessage(StoredKeyValueMessage message) {
        this.keyValueProcessor.resolving(message.getStoreType(), message.getKey(), message.getValue());
    }

    private void processGetStoredValueMessage(GetStoredValueMessage message, Peer sender) throws IOException {
        byte[] value = this.keyValueStores.getValue(message.getStoreType(), message.getKey());

        this.outputProcessor.postMessage(sender, new StoredKeyValueMessage(message.getStoreType(), message.getKey(), value));
    }

    private void processGetStatusMessage(Peer sender) {
        Block bestBlock = this.blockProcessor.getBestBlock();
        Status status = new Status(this.peer.getId(), this.networkConfiguration.getNetworkNumber(), bestBlock.getNumber(), bestBlock.getHash());
        StatusMessage statusMessage = new StatusMessage(status);

        this.outputProcessor.postMessage(sender, statusMessage);
    }

    private void processBlockMessage(BlockMessage message, Peer sender) throws IOException {
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

        if (nprocessed > 0)
            return;

        BlockHash blockHash = message.getBlock().getHash();
        BlockHash ancestorHash = this.blockProcessor.getUnknownAncestorHash(blockHash);

        if (ancestorHash != null && !ancestorHash.equals(blockHash))
            this.outputProcessor.postMessage(sender, new GetBlockByHashMessage(ancestorHash));
    }

    private void processTransactionMessage(TransactionMessage message, Peer sender) {
        List<Transaction> processed = this.transactionProcessor.processTransaction(message.getTransaction());

        if (this.outputProcessor == null)
            return;

        int nprocessed = 0;

        for (Transaction transaction: processed) {
            Message outputMessage = new TransactionMessage(transaction);

            if (nprocessed == 0 && sender != null)
                this.outputProcessor.postMessage(outputMessage, Collections.singletonList(sender.getId()));
            else
                this.outputProcessor.postMessage(outputMessage);

            nprocessed++;
        }
    }

    private void processStatusMessage(StatusMessage message, Peer sender) {
        if (message.getStatus().getNetworkNumber() != this.peerProcessor.getNetworkNumber())
            return;

        Hash senderId = sender.getId();

        this.peerProcessor.registerBestBlockNumber(senderId, message.getStatus().getNetworkNumber(), message.getStatus().getBestBlockNumber());

        long fromNumber = this.blockProcessor.getBestBlockNumber();

        long toNumber = Math.min(fromNumber + 10, this.peerProcessor.getPeerBestBlockNumber(senderId));

        for (long number = fromNumber + 1; number <= toNumber; number++)
            outputProcessor.postMessage(sender, new GetBlockByNumberMessage(number));

        if (fromNumber < toNumber)
            outputProcessor.postMessage(sender, GetStatusMessage.getInstance());
    }

    private void processGetBlockByHashMessage(GetBlockByHashMessage message, Peer sender) throws IOException {
        Block block = this.blockProcessor.getBlockByHash(message.getHash());

        if (block != null)
            outputProcessor.postMessage(sender, new BlockMessage(block));
    }

    private void processGetBlockByNumberMessage(GetBlockByNumberMessage message, Peer sender) throws IOException {
        Block block = this.blockProcessor.getBlockByNumber(message.getNumber());

        if (block != null)
            outputProcessor.postMessage(sender, new BlockMessage(block));
    }

    private void processTrieNodeMessage(TrieNodeMessage message) throws IOException {
        this.warpProcessor.processAccountNode(message.getTopHash(), message.getTrieNode());
    }
}
