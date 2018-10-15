package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.net.InputChannel;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.Message;

import java.util.List;

/**
 * Created by ajlopez on 14/10/2018.
 */
public class NodeProcessor implements InputChannel {
    private InputProcessor inputProcessor;
    private TransactionPool transactionPool;

    public NodeProcessor(BlockChain blockChain) {
        OrphanBlocks orphanBlocks = new OrphanBlocks();
        BlockProcessor blockProcessor = new BlockProcessor(blockChain, orphanBlocks);
        this.transactionPool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(this.transactionPool);
        PeerProcessor peerProcessor = new PeerProcessor();
        OutputProcessor outputProcessor = new OutputProcessor();
        MessageProcessor messageProcessor = new MessageProcessor(blockProcessor, transactionProcessor, peerProcessor, outputProcessor);
        this.inputProcessor = new InputProcessor(messageProcessor);
    }

    public void start() {
        this.inputProcessor.start();
    }

    public void stop() {
        this.inputProcessor.stop();
    }

    public void onEmpty(Runnable action) {
        this.inputProcessor.onEmpty(action);
    }

    public void postMessage(Peer sender, Message message) {
        this.inputProcessor.postMessage(sender, message);
    }

    public List<Transaction> getTransactions() {
        return this.transactionPool.getTransactions();
    }
}
