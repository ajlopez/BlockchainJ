package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.peers.PeerNode;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.TrieStore;

import java.util.List;

/**
 * Created by ajlopez on 14/10/2018.
 */
public class NodeProcessor implements PeerNode {
    private final NetworkConfiguration networkConfiguration;
    private final Peer peer;
    private final ReceiveProcessor receiveProcessor;
    private final SendProcessor sendProcessor;
    private final TransactionPool transactionPool;
    private final MinerProcessor minerProcessor;
    private final BlockProcessor blockProcessor;

    public NodeProcessor(NetworkConfiguration networkConfiguration, Peer peer, BlockChain blockChain, TrieStore accountTrieStore, Address coinbase) {
        this.networkConfiguration = networkConfiguration;
        this.peer = peer;
        OrphanBlocks orphanBlocks = new OrphanBlocks();
        this.blockProcessor = new BlockProcessor(blockChain, orphanBlocks);
        this.transactionPool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(this.transactionPool);
        PeerProcessor peerProcessor = new PeerProcessor();
        this.sendProcessor = new SendProcessor(this.peer);
        MessageProcessor messageProcessor = new MessageProcessor(this.blockProcessor, transactionProcessor, peerProcessor, this.sendProcessor);
        this.receiveProcessor = new ReceiveProcessor(messageProcessor);
        this.minerProcessor = new MinerProcessor(blockChain, this.transactionPool, new AccountStoreProvider(accountTrieStore), coinbase);
        this.minerProcessor.onMinedBlock(blk -> {
            this.postMessage(this.peer, new BlockMessage(blk));
        });
    }

    public Peer getPeer() {
        return this.peer;
    }

    public void startMessagingProcess() {
        this.receiveProcessor.start();
    }

    public void stopMessagingProcess() {
        this.receiveProcessor.stop();
    }

    public void startMiningProcess() {
        this.minerProcessor.start();
    }

    public void stopMiningProcess() {
        this.minerProcessor.stop();
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

    public void connectTo(PeerNode node) {
        this.sendProcessor.connectToPeer(node.getPeer(), node);
    }

    public Status getStatus() {
        Block bestBlock = this.blockProcessor.getBestBlock();

        return new Status(this.peer.getId(), this.networkConfiguration.getNetworkNumber(), bestBlock.getNumber(), bestBlock.getHash());
    }
}
