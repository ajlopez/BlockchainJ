package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessor implements Runnable {
    private BlockProcessor blockProcessor;
    private TransactionPool transactionPool;
    private List<Consumer<Block>> newMinedBlockConsumers = new ArrayList<>();
    private boolean stopped = false;

    public MinerProcessor(BlockProcessor blockProcessor, TransactionPool transactionPool) {
        this.blockProcessor = blockProcessor;
        this.transactionPool = transactionPool;
    }

    public void process() {
        Block bestBlock = this.blockProcessor.getBestBlock();
        Block newBlock = this.mineBlock(bestBlock, this.transactionPool);

        this.blockProcessor.processBlock(newBlock);

        emitNewMinedBlock(newBlock);
    }

    public void onNewMinedBlock(Consumer<Block> consumer) {
        this.newMinedBlockConsumers.add(consumer);
    }

    public Block mineBlock(Block parent, TransactionPool txpool) {
        return new Block(parent, txpool.getTransactions());
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop() {
        this.stopped = true;
    }

    public void run() {
        while (!this.stopped) {
            try {
                this.process();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void emitNewMinedBlock(Block block) {
        this.newMinedBlockConsumers.forEach(a -> a.accept(block));
    }
}
