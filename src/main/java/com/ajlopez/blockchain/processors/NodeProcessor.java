package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.bc.BlockValidator;
import com.ajlopez.blockchain.bc.ExtendedBlockInformation;
import com.ajlopez.blockchain.bc.ObjectContext;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.execution.BlockExecutor;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.peers.PeerNode;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.CodeStore;
import com.ajlopez.blockchain.store.KeyValueStores;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.vms.eth.TrieStorageProvider;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 14/10/2018.
 */
public class NodeProcessor implements PeerNode {
    private final NetworkConfiguration networkConfiguration;
    private final Peer peer;
    private final ReceiveProcessor receiveProcessor;
    private final SendProcessor sendProcessor;
    private final KeyValueProcessor keyValueProcessor;
    private final TransactionPool transactionPool;
    private final MinerProcessor minerProcessor;
    private final BlockProcessor blockProcessor;

    public NodeProcessor(NetworkConfiguration networkConfiguration, Peer peer, Address coinbase, ObjectContext objectContext) {
        KeyValueStores keyValueStores = objectContext.getKeyValueStores();
        Stores stores = objectContext.getStores();
        BlockChain blockChain = objectContext.getBlockChain();
        TransactionPool transactionPool = objectContext.getTransactionPool();

        AccountStoreProvider accountStoreProvider = stores.getAccountStoreProvider();
        TrieStorageProvider trieStorageProvider = stores.getTrieStorageProvider();
        CodeStore codeStore = stores.getCodeStore();

        this.networkConfiguration = networkConfiguration;
        this.peer = peer;

        OrphanBlocks orphanBlocks = new OrphanBlocks();
        this.transactionPool = transactionPool;

        this.blockProcessor = new BlockProcessor(blockChain, orphanBlocks, new BlockValidator(new BlockExecutor(accountStoreProvider, trieStorageProvider, codeStore)), this.transactionPool);

        TransactionProcessor transactionProcessor = new TransactionProcessor(this.transactionPool);
        PeerProcessor peerProcessor = new PeerProcessor(this.networkConfiguration.getNetworkNumber());

        this.sendProcessor = new SendProcessor(this.peer);
        this.keyValueProcessor = new KeyValueProcessor();

        // TODO inject warp processor
        MessageProcessor messageProcessor = new MessageProcessor(this.peer, this.networkConfiguration, this.blockProcessor, transactionProcessor, peerProcessor, this.sendProcessor, null, keyValueStores, keyValueProcessor);

        this.receiveProcessor = new ReceiveProcessor(messageProcessor);
        // TODO get block gas limit from config?
        this.minerProcessor = new MinerProcessor(blockChain, this.transactionPool, stores, coinbase, 12_000_000L);
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

    public void onNewBlock(Consumer<Block> consumer) {
        this.blockProcessor.onNewBlock(consumer);
    }

    public void onNewBestBlock(Consumer<Block> consumer) {
        this.blockProcessor.onNewBestBlock(consumer);
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

    public Status getStatus() throws IOException {
        ExtendedBlockInformation bestBlockinformation = this.blockProcessor.getBestBlockInformation();

        if (bestBlockinformation == null)
            return new Status(this.peer.getId(), this.networkConfiguration.getNetworkNumber(), BlockChain.NO_BEST_BLOCK_NUMBER, null, Difficulty.ONE);

        return new Status(this.peer.getId(), this.networkConfiguration.getNetworkNumber(), bestBlockinformation.getBlockNumber(), bestBlockinformation.getBlockHash(), bestBlockinformation.getTotalDifficulty());
    }

    // TODO review: only exposed for beam sync tests
    public SendProcessor getSendProcessor() {
        return this.sendProcessor;
    }

    // TODO review: only exposed for beam sync tests
    public KeyValueProcessor getKeyValueProcessor() {
        return this.keyValueProcessor;
    }
}
