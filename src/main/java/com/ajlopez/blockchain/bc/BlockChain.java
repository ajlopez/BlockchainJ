package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.Stores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 15/08/2017.
 */
public class BlockChain implements BlockProvider {
    public static final long NO_BEST_BLOCK_NUMBER = -1;

    private Block bestBlock;
    private Difficulty bestTotalDifficulty;

    private final BlockStore blockStore;
    private final BlocksInformationStore blockInformationStore;

    private List<Consumer<Block>> blockConsumers = new ArrayList<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public BlockChain(Stores stores) {
        this.blockStore = stores.getBlockStore();
        this.blockInformationStore = stores.getBlocksInformationStore();
    }

    public Block getBestBlock() {
        this.lock.readLock().lock();

        try {
            return this.bestBlock;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public long getBestBlockNumber() {
        this.lock.readLock().lock();

        try {
            if (this.bestBlock == null)
                return NO_BEST_BLOCK_NUMBER;

            return this.bestBlock.getNumber();
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean connectBlock(Block block) throws IOException {
        this.lock.writeLock().lock();

        try {
            if (isOrphan(block))
                return false;

            if (this.blockStore.containsBlock(block.getHash()))
                return true;

            Difficulty parentTotalDifficulty;

            if (block.getNumber() == 0)
                parentTotalDifficulty = Difficulty.ZERO;
            else
                parentTotalDifficulty = this.blockInformationStore.get(block.getNumber() - 1).getBlockInformation(block.getParentHash()).getTotalDifficulty();

            Difficulty totalDifficulty = parentTotalDifficulty.add(block.getCummulativeDifficulty());

            boolean isBetterBlock = this.isBetterBlock(block, totalDifficulty);

            this.saveBlock(block, totalDifficulty, isBetterBlock);

            if (isBetterBlock)
                this.saveBestBlock(block, totalDifficulty);

            this.emitBlock(block);

            return true;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private boolean isBetterBlock(Block block, Difficulty totalDifficulty) {
        if (this.bestBlock == null)
            return true;

        return totalDifficulty.compareTo(this.bestTotalDifficulty) > 0;
    }

    private void emitBlock(Block block) {
        for (Consumer<Block> consumer: this.blockConsumers)
            consumer.accept(block);
    }

    public void onBlock(Consumer<Block> consumer) {
        this.blockConsumers.add(consumer);
    }

    @Override
    public Block getBlockByHash(Hash hash) throws IOException {
        this.lock.readLock().lock();

        try {
            return this.blockStore.getBlock(hash);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean isChainedBlock(Hash hash) throws IOException {
        this.lock.readLock().lock();

        try {
            return this.blockStore.containsBlock(hash);
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public Block getBlockByNumber(long number) throws IOException {
        this.lock.readLock().lock();

        try {
            BlocksInformation blocksInformation = this.blockInformationStore.get(number);

            if (blocksInformation == null)
                return null;

            BlockInformation blockInformation = blocksInformation.getBlockOnChainInformation();

            if (blockInformation == null)
                return null;

            return this.blockStore.getBlock(blockInformation.getBlockHash());
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    private boolean isOrphan(Block block) throws IOException {
        if (block.getNumber() == 0)
            return false;

        return !blockStore.containsBlock(block.getParentHash());
    }

    private void saveBlock(Block block, Difficulty totalDifficulty, boolean isBetterBlock) throws IOException {
        if (!this.blockStore.containsBlock(block.getHash()))
            this.blockStore.saveBlock(block);

        BlocksInformation blocksInformation = this.blockInformationStore.get(block.getNumber());

        if (blocksInformation == null)
            blocksInformation = new BlocksInformation();

        blocksInformation.addBlockInformation(block.getHash(), totalDifficulty);

        if (isBetterBlock)
            blocksInformation.setBlockOnChain(block.getHash());

        this.blockInformationStore.put(block.getNumber(), blocksInformation);
    }

    private void saveBestBlock(Block block, Difficulty totalDifficulty) throws IOException {
        this.bestBlock = block;
        this.bestTotalDifficulty = totalDifficulty;

        while (block.getNumber() > 0 && !this.getBlockByNumber(block.getNumber() - 1).getHash().equals(block.getParentHash())) {
            block = this.blockStore.getBlock(block.getParentHash());
            BlocksInformation blocksInformation = this.blockInformationStore.get(block.getNumber());
            blocksInformation.setBlockOnChain(block.getHash());
            this.blockInformationStore.put(block.getNumber(), blocksInformation);
        }

        long n = this.bestBlock.getNumber() + 1;

        for (BlocksInformation blocksInformation = this.blockInformationStore.get(n); blocksInformation != null; blocksInformation = this.blockInformationStore.get(n)) {
            blocksInformation.noBlockOnChain();
            this.blockInformationStore.put(n, blocksInformation);
            n++;
        }

        this.blockInformationStore.putBestHeight(this.bestBlock.getNumber());
    }
}
