package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessor {
    private BlockChain blockChain;
    private TransactionPool transactionPool;
    private List<Consumer<Block>> minedBlockConsumers = new ArrayList<>();
    private boolean stopped = false;

    public MinerProcessor(BlockChain blockChain, TransactionPool transactionPool) {
        this.blockChain = blockChain;
        this.transactionPool = transactionPool;
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
        return new Block(parent, txpool.getTransactions());
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
