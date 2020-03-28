package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.Stores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 15/08/2017.
 */
public class BlockChain implements BlockProvider {
    public static final long NO_BEST_BLOCK_NUMBER = -1;

    private Block best;

    private final BlockHashStore blocksByHash;
    private final BlocksInformationStore blocksInformationStore;

    private List<Consumer<Block>> blockConsumers = new ArrayList<>();

    public BlockChain(Stores stores) {
        this.blocksByHash = stores.getBlockHashStore();
        this.blocksInformationStore = stores.getBlocksInformationStore();
    }

    public Block getBestBlock() {
        return this.best;
    }

    public long getBestBlockNumber() {
        if (this.best == null)
            return NO_BEST_BLOCK_NUMBER;

        return this.best.getNumber();
    }

    public boolean connectBlock(Block block) throws IOException {
        if (isOrphan(block))
            return false;

        if (this.blocksByHash.containsBlock(block.getHash()))
            return true;

        boolean isBetterBlock = this.isBetterBlock(block);

        // TODO use total difficulty
        this.saveBlock(block, block.getDifficulty(), isBetterBlock);

        if (isBetterBlock)
            this.saveBestBlock(block);

        this.emitBlock(block);

        return true;
    }

    private boolean isBetterBlock(Block block) {
        if (this.best == null)
            return true;

        // TODO use total difficulty
        return block.getNumber() > this.best.getNumber();
    }

    private void emitBlock(Block block) {
        for (Consumer<Block> consumer: this.blockConsumers)
            consumer.accept(block);
    }

    public void onBlock(Consumer<Block> consumer) {
        this.blockConsumers.add(consumer);
    }

    @Override
    public Block getBlockByHash(Hash hash) {
        return this.blocksByHash.getBlock(hash);
    }

    public boolean isChainedBlock(Hash hash) {
        return this.blocksByHash.containsBlock(hash);
    }

    @Override
    public Block getBlockByNumber(long number) throws IOException {
        BlocksInformation blocksInformation = this.blocksInformationStore.get(number);

        if (blocksInformation == null)
            return null;

        BlockInformation blockInformation = blocksInformation.getBlockOnChainInformation();

        if (blockInformation == null)
            return null;

        return this.blocksByHash.getBlock(blockInformation.getBlockHash());
    }

    private boolean isOrphan(Block block) {
        if (block.getNumber() == 0)
            return false;

        return !blocksByHash.containsBlock(block.getParentHash());
    }

    private void saveBlock(Block block, Difficulty totalDifficulty, boolean isBetterBlock) throws IOException {
        if (!this.blocksByHash.containsBlock(block.getHash()))
            this.blocksByHash.saveBlock(block);

        BlocksInformation blocksInformation = this.blocksInformationStore.get(block.getNumber());

        if (blocksInformation == null)
            blocksInformation = new BlocksInformation();

        blocksInformation.addBlockInformation(block.getHash(), totalDifficulty);

        if (isBetterBlock)
            blocksInformation.setBlockOnChain(block.getHash());

        this.blocksInformationStore.put(block.getNumber(), blocksInformation);
    }

    private void saveBestBlock(Block block) throws IOException {
        this.best = block;

        while (block.getNumber() > 0 && !this.getBlockByNumber(block.getNumber() - 1).getHash().equals(block.getParentHash())) {
            block = this.blocksByHash.getBlock(block.getParentHash());
            BlocksInformation blocksInformation = this.blocksInformationStore.get(block.getNumber());
            blocksInformation.setBlockOnChain(block.getHash());
            this.blocksInformationStore.put(block.getNumber(), blocksInformation);
        }
    }
}
