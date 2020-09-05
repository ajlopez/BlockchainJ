package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.store.Stores;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by ajlopez on 15/08/2017.
 */
public class BlockChain implements BlockProvider {
    public static final long NO_BEST_BLOCK_NUMBER = -1;

    private ExtendedBlockInformation bestBlockInformation;

    private final BlockStore blockStore;
    private final BlocksInformationStore blockInformationStore;

    private boolean initialized;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public BlockChain(Stores stores) {
        this.blockStore = stores.getBlockStore();
        this.blockInformationStore = stores.getBlocksInformationStore();
    }

    private void initialize() throws IOException {
        long bestHeight = this.blockInformationStore.getBestHeight();

        if (bestHeight >= 0) {
            BlocksInformation blocksInformation = this.blockInformationStore.get(bestHeight);
            BlockInformation blockInformation = blocksInformation.getBlockOnChain();

            Block bestBlock = this.blockStore.getBlock(blockInformation.getBlockHash());
            this.bestBlockInformation = new ExtendedBlockInformation(bestBlock, blockInformation.getTotalDifficulty());
        }

        initialized = true;
    }

    public ExtendedBlockInformation getBestBlockInformation() throws IOException {
        this.lock.readLock().lock();

        try {
            if (!initialized)
                initialize();

            return this.bestBlockInformation;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean connectBlock(Block block) throws IOException {
        this.lock.writeLock().lock();

        try {
            if (!initialized)
                initialize();

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

            boolean isBetterBlock = this.isBetterBlock(totalDifficulty);

            this.saveBlock(block, totalDifficulty, isBetterBlock);

            if (isBetterBlock)
                this.saveBestBlock(block, totalDifficulty);

            return true;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private boolean isBetterBlock(Difficulty totalDifficulty) {
        if (this.bestBlockInformation == null)
            return true;

        return totalDifficulty.compareTo(this.bestBlockInformation.getTotalDifficulty()) > 0;
    }

    @Override
    public Block getBlockByHash(BlockHash blockHash) throws IOException {
        this.lock.readLock().lock();

        try {
            if (!initialized)
                initialize();

            return this.blockStore.getBlock(blockHash);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean isChainedBlock(BlockHash hash) throws IOException {
        this.lock.readLock().lock();

        try {
            if (!initialized)
                initialize();

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
            if (!initialized)
                initialize();

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
        this.bestBlockInformation = new ExtendedBlockInformation(block, totalDifficulty);

        while (block.getNumber() > 0 && !this.getBlockByNumber(block.getNumber() - 1).getHash().equals(block.getParentHash())) {
            block = this.blockStore.getBlock(block.getParentHash());
            BlocksInformation blocksInformation = this.blockInformationStore.get(block.getNumber());
            blocksInformation.setBlockOnChain(block.getHash());
            this.blockInformationStore.put(block.getNumber(), blocksInformation);
        }

        long n = this.bestBlockInformation.getBlockNumber() + 1;

        for (BlocksInformation blocksInformation = this.blockInformationStore.get(n); blocksInformation != null; blocksInformation = this.blockInformationStore.get(n)) {
            blocksInformation.noBlockOnChain();
            this.blockInformationStore.put(n, blocksInformation);
            n++;
        }

        this.blockInformationStore.putBestHeight(this.bestBlockInformation.getBlockNumber());
    }
}
