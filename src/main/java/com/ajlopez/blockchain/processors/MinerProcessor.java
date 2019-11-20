package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.ExecutionContext;
import com.ajlopez.blockchain.execution.TopExecutionContext;
import com.ajlopez.blockchain.execution.TransactionExecutor;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.AccountStoreProvider;

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
    private final AccountStoreProvider accountStoreProvider;
    private final Address coinbase;

    private boolean stopped = false;

    public MinerProcessor(BlockChain blockChain, TransactionPool transactionPool, AccountStoreProvider accountStoreProvider, Address coinbase) {
        this.blockChain = blockChain;
        this.transactionPool = transactionPool;
        this.accountStoreProvider = accountStoreProvider;
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
        AccountStore accountStore = this.accountStoreProvider.retrieve(parentStateRootHash);
        // TODO set storage provider, code store
        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        List<Transaction> transactions = transactionExecutor.executeTransactions(this.transactionPool.getTransactions(), null);

        return new Block(parent, null, transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, this.coinbase, Difficulty.ONE);
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
