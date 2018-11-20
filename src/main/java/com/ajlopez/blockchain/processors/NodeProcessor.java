package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.net.MessageChannel;
import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.peers.PeerNode;

import java.util.List;

/**
 * Created by ajlopez on 14/10/2018.
 */
public class NodeProcessor implements PeerNode {
    private Peer peer;
    private ReceiveProcessor receiveProcessor;
    private SendProcessor sendProcessor;
    private TransactionPool transactionPool;

    public NodeProcessor(Peer peer, BlockChain blockChain) {
        this.peer = peer;
        OrphanBlocks orphanBlocks = new OrphanBlocks();
        BlockProcessor blockProcessor = new BlockProcessor(blockChain, orphanBlocks);
        this.transactionPool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(this.transactionPool);
        PeerProcessor peerProcessor = new PeerProcessor();
        this.sendProcessor = new SendProcessor(this.peer);
        MessageProcessor messageProcessor = new MessageProcessor(blockProcessor, transactionProcessor, peerProcessor, this.sendProcessor);
        this.receiveProcessor = new ReceiveProcessor(messageProcessor);
    }

    public Peer getPeer() {
        return this.peer;
    }

    public void start() {
        this.receiveProcessor.start();
    }

    public void stop() {
        this.receiveProcessor.stop();
    }

    public void onEmpty(Runnable action) {
        this.receiveProcessor.onEmpty(action);
    }

    public void postMessage(Peer sender, Message message) {
        this.receiveProcessor.postMessage(sender, message);
    }

    public List<Transaction> getTransactions() {
        return this.transactionPool.getTransactions();
    }

    public void connectTo(Peer peer, MessageChannel channel) {
        this.sendProcessor.connectToPeer(peer, channel);
    }
}
