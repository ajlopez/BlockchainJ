package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.*;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.vms.eth.BlockData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessor {
    private final BlockChain blockChain;
    private final TransactionPool transactionPool;
    private final List<Consumer<Block>> minedBlockConsumers = new ArrayList<>();
    private final Stores stores;
    private final Address coinbase;

    private boolean stopped = false;

    public MinerProcessor(BlockChain blockChain, TransactionPool transactionPool, Stores stores, Address coinbase) {
        this.blockChain = blockChain;
        this.transactionPool = transactionPool;
        this.stores = stores;
        this.coinbase = coinbase;
    }

    public Block process() throws IOException {
        Block bestBlock = this.blockChain.getBestBlock();

        Block newBlock = this.mineBlock(bestBlock);

        emitMinedBlock(newBlock);

        return newBlock;
    }

    public void onMinedBlock(Consumer<Block> consumer) {
        this.minedBlockConsumers.add(consumer);
    }

    public Block mineBlock(Block parent) throws IOException {
        Hash parentStateRootHash = parent.getHeader().getStateRootHash();
        AccountStore accountStore = this.stores.getAccountStoreProvider().retrieve(parentStateRootHash);
        ExecutionContext executionContext = new TopExecutionContext(accountStore, this.stores.getTrieStorageProvider(), this.stores.getCodeStore());
        // TODO evaluate to use BlockExecutor instead of TransactionExecutor
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);
        long timestamp = System.currentTimeMillis();
        // TODO use difficulty instead of a constant
        BlockData blockData = new BlockData(parent.getNumber() + 1, timestamp, this.coinbase, Difficulty.ONE);

        List<Transaction> transactions = this.transactionPool.getTransactions();
        List<TransactionReceipt> transactionReceipts = transactionExecutor.executeTransactions(transactions, blockData);

        // TODO use uncles
        return new Block(parent, null, transactions, BlockExecutionResult.calculateTransactionReceiptsHash(transactionReceipts), accountStore.getRootHash(), System.currentTimeMillis() / 1000, this.coinbase, Difficulty.ONE);
    }

    public void start() {
        new Thread(this::mineProcess).start();
    }

    public void stop() {
        this.stopped = true;
    }

    public void mineProcess() {
        while (!this.stopped) {
            try {
                this.process();
                // TOD sleep period by configuration
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void emitMinedBlock(Block block) {
        this.minedBlockConsumers.forEach(a -> a.accept(block));
    }
}
