package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.TopExecutionContext;
import com.ajlopez.blockchain.execution.TransactionExecutor;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.TrieStore;

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
    private final TrieStore trieStore;

    private boolean stopped = false;

    public MinerProcessor(BlockChain blockChain, TransactionPool transactionPool, TrieStore trieStore) {
        this.blockChain = blockChain;
        this.transactionPool = transactionPool;
        this.trieStore = trieStore;
    }

    public Block process() {
        Block bestBlock = this.blockChain.getBestBlock();

        Block newBlock = this.mineBlock(bestBlock, this.transactionPool);

        emitMinedBlock(newBlock);

        return newBlock;
    }

    public void onMinedBlock(Consumer<Block> consumer) {
        this.minedBlockConsumers.add(consumer);
    }

    public Block mineBlock(Block parent, TransactionPool txpool) {
        Hash parentStateRootHash = parent.getHeader().getStateRootHash();
        Trie trie = this.trieStore.retrieve(parentStateRootHash);
        AccountStore accountStore = new AccountStore(trie);
        TransactionExecutor transactionExecutor = new TransactionExecutor(accountStore);

        List<Transaction> transactions = transactionExecutor.executeTransactions(this.transactionPool.getTransactions());

        return new Block(parent, transactions, accountStore.getRootHash());
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
