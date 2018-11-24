package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessor {
    private BlockProcessor blockProcessor;
    private TransactionPool transactionPool;
    private List<Consumer<Block>> minedBlockConsumers = new ArrayList<>();
    private boolean stopped = false;

    public MinerProcessor(BlockProcessor blockProcessor, TransactionPool transactionPool) {
        this.blockProcessor = blockProcessor;
        this.transactionPool = transactionPool;
    }

    public void process() {
        Block bestBlock = this.blockProcessor.getBestBlock();
        Block newBlock = this.mineBlock(bestBlock, this.transactionPool);

        this.blockProcessor.processBlock(newBlock);

        emitMinerBlock(newBlock);
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

    private void emitMinerBlock(Block block) {
        this.minedBlockConsumers.forEach(a -> a.accept(block));
    }
}
